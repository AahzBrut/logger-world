package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.item.Item
import io.github.loggerworld.domain.item.ItemCategory
import io.github.loggerworld.domain.item.ItemQuality
import io.github.loggerworld.domain.item.ItemStat
import io.github.loggerworld.domain.item.ItemStats
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.dto.response.Description
import io.github.loggerworld.repository.item.ItemCategoryDescriptionRepository
import io.github.loggerworld.repository.item.ItemCategoryEquipmentSlotRepository
import io.github.loggerworld.repository.item.ItemCategoryQualityStatRepository
import io.github.loggerworld.repository.item.ItemQualityDescriptionRepository
import io.github.loggerworld.repository.item.ItemRepository
import io.github.loggerworld.repository.item.ItemStatDescriptionRepository
import io.github.loggerworld.repository.item.ItemStatsRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional


typealias LanguageDescriptions = MutableMap<Languages, Description>
typealias CategoryDescriptions = MutableMap<ItemCategories, LanguageDescriptions>
typealias QualityDescriptions = MutableMap<ItemQualities, LanguageDescriptions>
typealias ItemStatDescriptions = MutableMap<ItemStatEnum, LanguageDescriptions>
typealias ItemCategoryStats = MutableMap<ItemCategories, ItemQualityStats>
typealias ItemQualityStats = MutableMap<ItemQualities, ItemLevelStats>
typealias ItemStatValues = MutableMap<ItemStatEnum, Pair<Float, Float>>
typealias ItemLevelStats = MutableMap<Byte, ItemStatValues>
typealias ItemCategoryEquipmentSlots = MutableMap<ItemCategories, MutableSet<EquipmentSlotTypes>>

@Service
class ItemDomainService(
    private val itemCategoryQualityStatRepository: ItemCategoryQualityStatRepository,
    private val itemCategoryDescriptionRepository: ItemCategoryDescriptionRepository,
    private val itemQualityDescriptionRepository: ItemQualityDescriptionRepository,
    private val itemStatDescriptionRepository: ItemStatDescriptionRepository,
    private val itemRepository: ItemRepository,
    private val itemStatsRepository: ItemStatsRepository,
    private val itemCategoryEquipmentSlotRepository: ItemCategoryEquipmentSlotRepository,
) {

    val categoryDescriptions: CategoryDescriptions = mutableMapOf()
    val qualityDescriptions: QualityDescriptions = mutableMapOf()
    val statDescriptions: ItemStatDescriptions = mutableMapOf()
    val itemCategoryStats: ItemCategoryStats = mutableMapOf()
    val itemCategoryEquipmentSlots: ItemCategoryEquipmentSlots = mutableMapOf()

    @Transactional
    fun initCategoryDescriptions() {
        categoryDescriptions.clear()
        itemCategoryDescriptionRepository.findAll()
            .forEach {
                val languageDescriptions = categoryDescriptions
                    .computeIfAbsent(it.itemCategory.code)
                    { mutableMapOf() }
                languageDescriptions[it.language.code] = Description(it.name, it.description)
            }
    }

    @Transactional
    fun initQualityDescriptions() {
        qualityDescriptions.clear()
        itemQualityDescriptionRepository.findAll()
            .forEach {
                val languageDescriptions = qualityDescriptions
                    .computeIfAbsent(it.itemQuality.code)
                    { mutableMapOf() }
                languageDescriptions[it.language.code] = Description(it.name, it.description)
            }
    }

    @Transactional
    fun initItemStatDescriptions() {
        statDescriptions.clear()
        itemStatDescriptionRepository.findAll()
            .forEach {
                val languageDescriptions = statDescriptions
                    .computeIfAbsent(it.itemStat.code)
                    { mutableMapOf() }
                languageDescriptions[it.language.code] = Description(it.name, it.description)
            }
    }

    @Transactional
    fun initCategoryStats() {
        itemCategoryStats.clear()
        itemCategoryQualityStatRepository.findAll()
            .forEach {
                val itemQualityStats = itemCategoryStats
                    .computeIfAbsent(it.itemCategoryQuality.itemCategory.code)
                    { mutableMapOf() }
                val itemLevelStats = itemQualityStats
                    .computeIfAbsent(it.itemCategoryQuality.itemQuality.code)
                    { mutableMapOf() }
                val itemStatValues = itemLevelStats
                    .computeIfAbsent(it.level)
                    { mutableMapOf() }
                itemStatValues[it.itemStat.code] = Pair(it.minValue, it.maxValue)
            }
    }

    @Transactional
    fun createItem(itemData: ItemData) {
        val item = Item(ItemCategory(itemData.category), ItemQuality(itemData.quality), itemData.quantity)
        itemRepository.save(item)
        itemData.id = item.id!!
    }

    @Transactional
    fun createItemStats(itemData: ItemData) {
        val stats = itemData.stats.entries.map {
            ItemStats(Item(itemData.id), ItemStat(it.key), it.value)
        }.toList()

        itemStatsRepository.saveAll(stats)
    }

    @Transactional
    fun initCategoryEquipmentSlots() {
        itemCategoryEquipmentSlots.clear()
        itemCategoryEquipmentSlotRepository.findAll()
            .forEach {
                val equipmentSlotTypes = itemCategoryEquipmentSlots
                    .computeIfAbsent(it.itemCategory.code)
                    { mutableSetOf() }
                equipmentSlotTypes.add(it.equipmentSlotType.code)
            }
    }
}
