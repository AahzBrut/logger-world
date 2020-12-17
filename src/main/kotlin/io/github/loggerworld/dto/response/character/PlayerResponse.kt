package io.github.loggerworld.dto.response.character

data class PlayerResponse (
    var id: Long = -1,
    var userId: Long = -1,
    var name: String = "",
    var classId: Byte = -1,
    var locationId: Short = -1,
    var baseStats: Map<Byte, Float> = emptyMap(),
    var effectiveStats: Map<Byte, Float> = emptyMap(),
    var baseAttributes: Map<Byte, Float> = emptyMap(),
    var effectiveAttributes: Map<Byte, Float> = emptyMap(),
)