package io.github.loggerworld.dto.event

data class StartGameEvent(
    var playerId: Long,
    var locationId: Short
)