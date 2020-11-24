package io.github.loggerworld.dto.response.character

data class ShortPlayerResponse(
    var id: Long = -1,
    var name: String = "",
    var level: Byte = -1,
    var classId: Byte = -1
)
