package io.github.loggerworld.ecs

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.dto.response.geography.LocationResponse
import io.github.loggerworld.dto.response.geography.LocationTypeResponse
import io.github.loggerworld.dto.response.geography.LocationTypesResponse
import io.github.loggerworld.dto.response.geography.LocationsResponse
import io.github.loggerworld.service.LocationService
import io.github.loggerworld.service.PlayerService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.stereotype.Component

@Component
class WorldCache(
    locationService: LocationService,
    playerService: PlayerService
) : LogAware {
    final val locationTypeDescriptions = locationService.getAllLocationTypeDescriptions()
    final val locationDescriptions = locationService.getAllLocationDescriptions()
    final val worldMap = locationService.getWorldMap(locationDescriptions, locationTypeDescriptions)
    final val playerClassDescriptions = playerService.getAllPlayerClassDescriptions()
    final val playerStatDescriptions = playerService.getAllPlayerStatDescriptions()

    fun getLocationTypes(userLanguage: Languages): LocationTypesResponse {

        logger().debug("User language: $userLanguage")

        return LocationTypesResponse(
            locationTypeDescriptions.entries.map {
                LocationTypeResponse(
                    it.key.ordinal.toByte(),
                    (it.value[userLanguage]
                        ?: error("Language $userLanguage not found in location types cache")).first,
                    (it.value[userLanguage]
                        ?: error("Language $userLanguage not found in location types cache")).second)
            }
                .toList())
    }

    fun getLocations(userLanguage: Languages): LocationsResponse {

        return LocationsResponse(
            worldMap.locations.entries.map {
                LocationResponse(
                    it.key,
                    it.value.type.ordinal.toByte(),
                    it.value.xCoord,
                    it.value.yCoord,
                    ((locationDescriptions[it.key]
                        ?: error("There is no location with id: ${it.key} in location description cache"))[userLanguage]
                        ?: error("There is no language: $userLanguage in location descriptions cache.")).first,
                    ((locationDescriptions[it.key]
                        ?: error("There is no location with id: ${it.key} in location description cache"))[userLanguage]
                        ?: error("There is no language: $userLanguage in location descriptions cache.")).second)
            }.toList())
    }
}