package io.github.loggerworld.dto.response.item

data class ItemCategoryResponse(
    var id: Int,
    var parentId: Int?,
    var isItem: Boolean,
    var code: String,
    var name: String,
    var description: String,
    var stats: Set<Int>,
    var equipmentSlots: Set<Int>,
)
