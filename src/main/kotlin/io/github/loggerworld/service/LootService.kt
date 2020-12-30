package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.dto.inner.loot.LootDropData
import io.github.loggerworld.service.domain.LootDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.random.Random

@Service
class LootService(
    private val lootDomainService: LootDomainService,
    private val itemService: ItemService,
) : LogAware {

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache() {
        lootDomainService.initLootMobs()
        logger().debug("Loot settings are cached")
    }

    fun getLootFor(monsterClass: MonsterClasses, monsterType: MonsterTypes, level: Byte): List<ItemData> {
        return lootDomainService
            .lootMobs[LootDropData(monsterClass, monsterType, level)]!!
            .filter {
                it.key.probability > Random.nextFloat()
            }
            .flatMap {
                val qualitySelector = Random.nextFloat()
                it.value
                    .filter { qualityData ->
                        qualityData.probFrom <= qualitySelector && qualityData.probTo > qualitySelector
                    }
                    .map { qualityData ->
                        val quantity = if (qualityData.quantityMin.toInt() != qualityData.quantityMax.toInt())
                            Random.nextInt(qualityData.quantityMin.toInt(), qualityData.quantityMax.toInt()).toLong()
                        else
                            qualityData.quantityMin.toLong()
                        val categories = it.key.itemCategory.getChildItems()
                        val category = if (categories.size == 1) categories[0] else categories[Random.nextInt(0, categories.size)]
                        itemService.createItem(category, qualityData.itemQuality, level, quantity)
                    }
            }

    }
}