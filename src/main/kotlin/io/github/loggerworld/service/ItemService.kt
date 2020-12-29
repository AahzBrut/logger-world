package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum
import io.github.loggerworld.domain.enums.ItemStatEnum.DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.STACK_SIZE
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.dto.response.item.ItemCategoriesResponse
import io.github.loggerworld.dto.response.item.ItemCategoryResponse
import io.github.loggerworld.dto.response.item.ItemEquipmentSlotResponse
import io.github.loggerworld.dto.response.item.ItemEquipmentSlotsResponse
import io.github.loggerworld.dto.response.item.ItemQualitiesResponse
import io.github.loggerworld.dto.response.item.ItemQualityResponse
import io.github.loggerworld.dto.response.item.ItemStatResponse
import io.github.loggerworld.dto.response.item.ItemStatsResponse
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.DeserializeItemsDropFromMobCommand
import io.github.loggerworld.messagebus.event.InventoryChangedEvent
import io.github.loggerworld.messagebus.event.SerializeItemsDropFromMobCommand
import io.github.loggerworld.service.domain.EquipmentDomainService
import io.github.loggerworld.service.domain.ItemDomainService
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE
import io.github.loggerworld.util.logger
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct
import kotlin.random.Random


@Service
class ItemService(
    private val itemDomainService: ItemDomainService,
    private val serializeCommandBus: EventBus<SerializeItemsDropFromMobCommand>,
    private val deserializeCommandBus: EventBus<DeserializeItemsDropFromMobCommand>,
    private val performanceCounter: PerformanceCounter,
    private val inventoryChangedEventBus: EventBus<InventoryChangedEvent>,
    private val simpleMessagingTemplate: SimpMessagingTemplate,
    private val playerService: PlayerService,
    private val userService: UserService,
    private val equipmentDomainService: EquipmentDomainService,
) : LogAware {

    private val itemIdCounter: AtomicLong = AtomicLong(-1L)
    val itemCategoryEquipmentSlots by lazy { itemDomainService.itemCategoryEquipmentSlots }

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache() {
        itemDomainService.initCategoryDescriptions()
        itemDomainService.initQualityDescriptions()
        itemDomainService.initItemStatDescriptions()
        itemDomainService.initCategoryStats()
        itemDomainService.initCategoryEquipmentSlots()
        logger().debug("Item definitions cache populated")
    }

    fun createItem(category: ItemCategories, quality: ItemQualities, level: Byte, quantity: Long): ItemData {
        val itemGeneratorStats = itemDomainService.itemCategoryStats[category]!![quality]!![level]!!
        val itemStats = itemGeneratorStats.entries.associate {
            it.key to getRandomValue(it.value.first, it.value.second)
        }.toMutableMap()
        if (category.applicableStats.contains(DURABILITY)) itemStats[DURABILITY] = itemStats[MAX_DURABILITY]!!
        logger().debug("Item '$category' generated with stats: $itemStats")
        return ItemData(
            itemIdCounter.getAndDecrement(),
            category,
            quality,
            quantity,
            itemStats,
            category.applicableStats.contains(STACK_SIZE)
        )
    }

    fun decodeItem(item: String, language: Languages): String {
        val ids = item.split(",").map(String::toInt)
        val category = ItemCategories.values()[ids[0]]
        val quality = ItemQualities.values()[ids[1]]

        return if (category.getAllParents().contains(ItemCategories.VALUABLES))
            "${ids[2]} ${itemDomainService.categoryDescriptions[category]!![language]!!.short}"
        else
            "${ids[2]} ${itemDomainService.categoryDescriptions[category]!![language]!!.short} (${itemDomainService.qualityDescriptions[quality]!![language]!!.short})"
    }


    private fun getRandomValue(first: Float, second: Float): Float {
        return if (first.toInt() != second.toInt())
            Random.nextInt(first.toInt(), second.toInt()).toFloat()
        else
            first
    }

    @Suppress("kotlin:S1144")
    @Scheduled(fixedDelay = 10, initialDelay = 100)
    private fun serializeItems() {
        performanceCounter.start(PerfCounters.ITEM_SERIALIZER_SERVICE)

        while (serializeCommandBus.receiveEvent { event ->
                event.items.forEach { itemData ->
                    itemDomainService.createItem(itemData)
                    itemDomainService.createItemStats(itemData)
                    logger().debug("\n\nItem saved:$itemData")
                }

                deserializeCommandBus.dispatchEvent {
                    it.playerId = event.playerId
                    it.monsterClass = event.monsterClass
                    it.monsterType = event.monsterType
                    it.monsterLevel = event.monsterLevel
                    it.items = event.items
                }
            }) {
            // Empty block
        }

        performanceCounter.stop(PerfCounters.ITEM_SERIALIZER_SERVICE)
    }

    @Suppress("kotlin:S1144")
    @Scheduled(fixedDelay = 10, initialDelay = 100)
    private fun notifyInventoryChanged() {
        performanceCounter.start(PerfCounters.INVENTORY_CHANGE_NOTIFICATION_SERVICE)

        while (inventoryChangedEventBus.receiveEvent {
                val player = playerService.getPlayerById(it.playerId)
                val user = userService.getUserById(player.userId)

                simpleMessagingTemplate.convertAndSendToUser(user.loginName, WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE, it)
            }) {
            // Empty body
        }

        performanceCounter.stop(PerfCounters.INVENTORY_CHANGE_NOTIFICATION_SERVICE)
    }

    fun getAllStats(userName: String): ItemStatsResponse {
        val user = userService.getUserByName(userName)
        return ItemStatsResponse(
            ItemStatEnum.values().map {
                ItemStatResponse(
                    it.ordinal,
                    it.name,
                    itemDomainService.statDescriptions[it]!![user.language]!!.short,
                    itemDomainService.statDescriptions[it]!![user.language]!!.full,
                )
            }
        )
    }

    fun getAllCategories(userName: String): ItemCategoriesResponse {
        val user = userService.getUserByName(userName)
        return ItemCategoriesResponse(
            ItemCategories.values().map {category->
                ItemCategoryResponse(
                    category.ordinal,
                    category.parent?.ordinal,
                    category.isItem,
                    category.name,
                    itemDomainService.categoryDescriptions[category]!![user.language]!!.short,
                    itemDomainService.categoryDescriptions[category]!![user.language]!!.full,
                    category.applicableStats.map {it.ordinal}.toSet(),
                    itemCategoryEquipmentSlots[category]?.map { it.ordinal }?.toSet() ?: emptySet()
                )
            }
        )
    }

    fun getAllQualities(userName: String): ItemQualitiesResponse {
        val user = userService.getUserByName(userName)
        return ItemQualitiesResponse(
            ItemQualities.values().map {
                ItemQualityResponse(
                    it.ordinal,
                    it.name,
                    itemDomainService.qualityDescriptions[it]!![user.language]!!.short,
                    itemDomainService.qualityDescriptions[it]!![user.language]!!.full,
                )
            }
        )
    }

    fun getAllEquipmentSlots(userName: String): ItemEquipmentSlotsResponse {
        val user = userService.getUserByName(userName)
        return ItemEquipmentSlotsResponse(
            EquipmentSlotTypes.values().map {
                ItemEquipmentSlotResponse(
                    it.ordinal,
                    it.name,
                    equipmentDomainService.equipmentSlotTypeDescriptions[it]!![user.language]!!.short,
                    equipmentDomainService.equipmentSlotTypeDescriptions[it]!![user.language]!!.full,
                )
            }
        )
    }
}