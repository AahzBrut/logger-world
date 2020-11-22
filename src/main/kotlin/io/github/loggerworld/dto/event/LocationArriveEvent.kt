package io.github.loggerworld.dto.event

data class LocationArriveEvent(
    var playerId: Long = -1,
    var locationId: Short = -1
)