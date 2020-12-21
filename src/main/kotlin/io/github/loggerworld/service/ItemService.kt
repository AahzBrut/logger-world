package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum.DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DURABILITY
import io.github.loggerworld.service.domain.ItemDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.random.Random


@Service
class ItemService(
    private val itemDomainService: ItemDomainService
) : LogAware {

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache() {
        itemDomainService.initCategoryDescriptions()
        itemDomainService.initQualityDescriptions()
        itemDomainService.initItemStatDescriptions()
        itemDomainService.initCategoryStats()
        logger().debug("Item definitions cache populated")
    }

    fun createItem(category: ItemCategories, quality: ItemQualities, level: Byte, quantity: Long) {
        val itemGeneratorStats = itemDomainService.itemCategoryStats[category]!![quality]!![level]!!
        val itemId = itemDomainService.createItem(category, quality, quantity)
        val itemStats = itemGeneratorStats.entries.associate {
            it.key to getRandomValue(it.value.first, it.value.second)
        }.toMutableMap()
        if (category.applicableStats.contains(DURABILITY)) itemStats[DURABILITY] = itemStats[MAX_DURABILITY]!!
        logger().debug("Item '$category' generated with stats: $itemStats")
        itemDomainService.createItemStats(itemId, itemStats)
    }

    private fun getRandomValue(first: Float, second: Float): Float {
        return if (first.toInt() != second.toInt())
            Random.nextInt(first.toInt(), second.toInt()).toFloat()
        else
            first
    }
}