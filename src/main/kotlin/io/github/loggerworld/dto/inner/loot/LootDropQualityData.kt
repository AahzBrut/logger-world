package io.github.loggerworld.dto.inner.loot

import io.github.loggerworld.domain.enums.ItemQualities

data class LootDropQualityData(
    val itemQuality: ItemQualities,
    val probFrom: Float,
    val probTo: Float,
    val quantityMin: Float,
    val quantityMax: Float
)
