package io.github.loggerworld.dto.inner.item

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum

data class ItemData(
    @JsonProperty("1")
    var id: Long = 0,
    @JsonProperty("2")
    val category: ItemCategories = ItemCategories.NOTHING,
    @JsonProperty("3")
    val quality: ItemQualities = ItemQualities.NONE,
    @JsonProperty("4")
    var quantity: Long = 0,
    @JsonProperty("5")
    val stats: Map<ItemStatEnum, Float> = emptyMap(),
    @JsonProperty("6")
    val stackable: Boolean = false
)
