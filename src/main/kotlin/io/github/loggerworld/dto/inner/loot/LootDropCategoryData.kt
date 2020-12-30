package io.github.loggerworld.dto.inner.loot

import io.github.loggerworld.domain.enums.ItemCategories

data class LootDropCategoryData(
    val itemCategory: ItemCategories,
    val probability: Float
)
