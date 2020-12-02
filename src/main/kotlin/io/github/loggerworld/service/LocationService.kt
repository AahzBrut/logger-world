package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.dto.inner.WorldMap
import io.github.loggerworld.dto.response.geography.LocationResponse
import io.github.loggerworld.dto.response.geography.LocationTypeResponse
import io.github.loggerworld.dto.response.geography.LocationTypesResponse
import io.github.loggerworld.dto.response.geography.LocationsResponse
import io.github.loggerworld.service.domain.LocationDomainService
import io.github.loggerworld.service.domain.UserDomainService
import org.springframework.stereotype.Service

typealias LocationDescriptionsMap = Map<Short, Map<Languages, Pair<String, String>>>
typealias LocationTypeDescriptionsMap = Map<LocationTypes, Map<Languages, Pair<String, String>>>

@Service
class LocationService(
    private val locationDomainService: LocationDomainService,
    private val userDomainService: UserDomainService,
) {

    fun getLocationTypes(userLanguage: Languages): LocationTypesResponse {

        return LocationTypesResponse(
            getAllLocationTypeDescriptions().entries.map {
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
            getWorldMap().locations.entries.map {
                LocationResponse(
                    it.key,
                    it.value.type.ordinal.toByte(),
                    it.value.xCoord,
                    it.value.yCoord,
                    ((getAllLocationDescriptions()[it.key]
                        ?: error("There is no location with id: ${it.key} in location description cache"))[userLanguage]
                        ?: error("There is no language: $userLanguage in location descriptions cache.")).first,
                    ((getAllLocationDescriptions()[it.key]
                        ?: error("There is no location with id: ${it.key} in location description cache"))[userLanguage]
                        ?: error("There is no language: $userLanguage in location descriptions cache.")).second)
            }.toList())
    }


    fun getWorldMap() : WorldMap{
        val allLocations = locationDomainService.getAllLocations()
        val locationDescriptions = getAllLocationDescriptions()
        val locationTypeDescriptions = getAllLocationTypeDescriptions()

        allLocations.forEach { mapEntry ->
            mapEntry.value.descriptions = locationDescriptions[mapEntry.key] ?: error("There is no descriptions of location with id: ${mapEntry.key}")
            mapEntry.value.typeDescriptions = locationTypeDescriptions[mapEntry.value.type] ?: error("There is no type descriptions of location with id: ${mapEntry.key}")
        }

        return WorldMap(allLocations)
    }

    fun getAllLocationDescriptions() =
        locationDomainService.getAllLocationDescriptions()

    fun getAllLocationTypeDescriptions() =
        locationDomainService.getAllLocationTypeDescriptions()

    fun getAllLocationMonsterSpawners() = Unit
}