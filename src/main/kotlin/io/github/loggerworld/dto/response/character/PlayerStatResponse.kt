package io.github.loggerworld.dto.response.character

data class PlayerStatResponse(
    var id: Byte = -1,
    var code: String = "",
    var name: String = "",
    var description: String = "",
)
