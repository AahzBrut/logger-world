package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.loot.LootDrop
import io.github.loggerworld.domain.loot.LootDropCategory
import io.github.loggerworld.dto.inner.loot.LootDropCategoryData
import io.github.loggerworld.dto.inner.loot.LootDropData
import io.github.loggerworld.dto.inner.loot.LootDropQualityData
import io.github.loggerworld.repository.loot.LootDropRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional


typealias LootMobs = MutableMap<LootDropData, LootCategories>
typealias LootCategories = MutableMap<LootDropCategoryData, LootQualities>
typealias LootQualities = MutableList<LootDropQualityData>


@Service
class LootDomainService(
    private val lootDropRepository: LootDropRepository,
) {

    val lootMobs: LootMobs = mutableMapOf()


    @Transactional
    fun initLootMobs() {
        lootMobs.clear()
        lootDropRepository.findAll().forEach {
            lootMobs[LootDropData(it.monsterClass.code, it.monsterType.code, it.level)] = getLootCategories(it)
        }
    }

    private fun getLootCategories(lootDrop: LootDrop): LootCategories {
        val result: LootCategories = mutableMapOf()
        lootDrop.categories.forEach {
            result[LootDropCategoryData(it.itemCategory.code, it.probability / 100f)] = getLootQualities(it)
        }
        return result
    }

    private fun getLootQualities(lootDropCategory: LootDropCategory): LootQualities {
        val result: LootQualities = mutableListOf()
        var prob = 0f
        lootDropCategory.qualities.forEach {
            result += LootDropQualityData(
                itemQuality = it.itemQuality.code,
                probFrom = prob / 100f,
                probTo = (prob + it.probability) / 100f,
                it.quantityMin,
                it.quantityMax
            )
            prob += it.probability
        }
        return result
    }
}
