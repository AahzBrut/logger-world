package io.github.loggerworld.dto.inner

data class WorldMap(
    var locations: Map<Short, WorldLocation> = emptyMap()
)