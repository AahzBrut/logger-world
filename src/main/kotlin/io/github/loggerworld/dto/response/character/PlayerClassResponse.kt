package io.github.loggerworld.dto.response.character

data class PlayerClassResponse(
    var id: Byte = -1,
    var code: String = "",
    var stats: Map<Byte, Float> = emptyMap(),
)
