package io.github.loggerworld.dto.inner.item

import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum

data class ItemData(
    var id: Long = 0,
    val category: ItemCategories = ItemCategories.NOTHING,
    val quality: ItemQualities = ItemQualities.NONE,
    var quantity: Long = 0,
    val stats: Map<ItemStatEnum, Float> = emptyMap(),
    val stackable: Boolean = false
)
