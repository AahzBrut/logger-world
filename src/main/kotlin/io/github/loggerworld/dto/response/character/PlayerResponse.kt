package io.github.loggerworld.dto.response.character

data class PlayerResponse (
    var id: Long = -1,
    var userId: Long = -1,
    var name: String = "",
    var classId: Byte = -1,
    var locationId: Short = -1
)