package io.github.loggerworld.dto.response.character

data class PlayerResponse (
    var id: Long = -1,
    var name: String = "",
    var playerClass: String = ""
)