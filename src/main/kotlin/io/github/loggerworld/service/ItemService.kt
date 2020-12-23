package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum.DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.STACK_SIZE
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.SerializeItemsDropFromMobCommand
import io.github.loggerworld.service.domain.ItemDomainService
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct
import kotlin.random.Random


@Service
class ItemService(
    private val itemDomainService: ItemDomainService,
    @Qualifier("Serializer")
    private val serializeCommandBus: EventBus<SerializeItemsDropFromMobCommand>,
    @Qualifier("Deserializer")
    private val deserializeCommandBus: EventBus<SerializeItemsDropFromMobCommand>,
    private val performanceCounter: PerformanceCounter,
) : LogAware {

    private val itemIdCounter: AtomicLong = AtomicLong(-1L)

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache() {
        itemDomainService.initCategoryDescriptions()
        itemDomainService.initQualityDescriptions()
        itemDomainService.initItemStatDescriptions()
        itemDomainService.initCategoryStats()
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
}