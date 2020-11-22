package io.github.loggerworld.ecs

import io.github.loggerworld.service.LocationService
import io.github.loggerworld.service.PlayerService
import org.springframework.stereotype.Component

@Component
class WorldCache(
    locationService: LocationService,
    playerService: PlayerService
) {
    final val locationTypeDescriptions = locationService.getAllLocationTypeDescriptions()
    final val locationDescriptions = locationService.getAllLocationDescriptions()
    final val worldMap = locationService.getWorldMap(locationDescriptions, locationTypeDescriptions)
    final val playerClassDescriptions = playerService.getAllPlayerClassDescriptions()
    final val playerStatDescriptions = playerService.getAllPlayerStatDescriptions()
}