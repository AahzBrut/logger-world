package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.item.Item
import io.github.loggerworld.domain.item.ItemCategory
import io.github.loggerworld.domain.item.ItemQuality
import io.github.loggerworld.domain.item.ItemStat
import io.github.loggerworld.domain.item.ItemStats
import io.github.loggerworld.dto.response.Description
import io.github.loggerworld.repository.item.ItemCategoryDescriptionRepository
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

@Service
class ItemDomainService(
    private val itemCategoryQualityStatRepository: ItemCategoryQualityStatRepository,
    private val itemCategoryDescriptionRepository: ItemCategoryDescriptionRepository,
    private val itemQualityDescriptionRepository: ItemQualityDescriptionRepository,
    private val itemStatDescriptionRepository: ItemStatDescriptionRepository,
    private val itemRepository: ItemRepository,
    private val itemStatsRepository: ItemStatsRepository,
) {

    val categoryDescriptions: CategoryDescriptions = mutableMapOf()
    val qualityDescriptions: QualityDescriptions = mutableMapOf()
    val statDescriptions: ItemStatDescriptions = mutableMapOf()
    val itemCategoryStats: ItemCategoryStats = mutableMapOf()

    @Transactional
    fun initCategoryDescriptions(){
        categoryDescriptions.clear()
        itemCategoryDescriptionRepository.findAll()
            .forEach {
                if (!categoryDescriptions.containsKey(it.itemCategory.code)) {
                    categoryDescriptions[it.itemCategory.code] = mutableMapOf()
                }
                categoryDescriptions[it.itemCategory.code]!![it.language.code] = Description(it.name, it.description)
            }
    }

    @Transactional
    fun initQualityDescriptions(){
        qualityDescriptions.clear()
        itemQualityDescriptionRepository.findAll()
            .forEach {
                if (!qualityDescriptions.containsKey(it.itemQuality.code)) {
                    qualityDescriptions[it.itemQuality.code] = mutableMapOf()
                }
                qualityDescriptions[it.itemQuality.code]!![it.language.code] = Description(it.name, it.description)
            }
    }

    @Transactional
    fun initItemStatDescriptions(){
        statDescriptions.clear()
        itemStatDescriptionRepository.findAll()
            .forEach {
                if (!statDescriptions.containsKey(it.itemStat.code)) {
                    statDescriptions[it.itemStat.code] = mutableMapOf()
                }
                statDescriptions[it.itemStat.code]!![it.language.code] = Description(it.name, it.description)
            }
    }

    @Transactional
    fun initCategoryStats(){
        itemCategoryStats.clear()
        itemCategoryQualityStatRepository.findAll()
            .forEach {
                if (!itemCategoryStats.containsKey(it.itemCategoryQuality.itemCategory.code))
                    itemCategoryStats[it.itemCategoryQuality.itemCategory.code] = mutableMapOf()
                if (!itemCategoryStats[it.itemCategoryQuality.itemCategory.code]!!.containsKey(it.itemCategoryQuality.itemQuality.code))
                    itemCategoryStats[it.itemCategoryQuality.itemCategory.code]!![it.itemCategoryQuality.itemQuality.code] = mutableMapOf()
                if (!itemCategoryStats[it.itemCategoryQuality.itemCategory.code]!![it.itemCategoryQuality.itemQuality.code]!!.containsKey(it.level))
                    itemCategoryStats[it.itemCategoryQuality.itemCategory.code]!![it.itemCategoryQuality.itemQuality.code]!![it.level] = mutableMapOf()
                itemCategoryStats[it.itemCategoryQuality.itemCategory.code]!![it.itemCategoryQuality.itemQuality.code]!![it.level]!![it.itemStat.code] = Pair(it.minValue, it.maxValue)
            }
    }

    @Transactional
    fun createItem(category: ItemCategories, quality: ItemQualities, quantity: Long): Long {
        val item = Item(ItemCategory(category), ItemQuality(quality), quantity)
        itemRepository.save(item)
        return item.id!!
    }

    @Transactional
    fun createItemStats(itemId: Long, itemStats: MutableMap<ItemStatEnum, Float>) {
        val stats = itemStats.entries.map {
            ItemStats(Item(itemId), ItemStat(it.key), it.value)
        }.toList()

        itemStatsRepository.saveAll(stats)
    }
}
