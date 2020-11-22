package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.dto.inner.WorldMap
import io.github.loggerworld.service.domain.LocationDomainService
import org.springframework.stereotype.Service

typealias LocationDescriptionsMap = Map<Short, Map<Languages, Pair<String, String>>>
typealias LocationTypeDescriptionsMap = Map<LocationTypes, Map<Languages, Pair<String, String>>>

@Service
class LocationService(
    private val locationDomainService: LocationDomainService
) {

    fun getWorldMap(locationDescriptions: LocationDescriptionsMap, locationTypeDescriptions: LocationTypeDescriptionsMap) : WorldMap{
        val allLocations = locationDomainService.getAllLocations()

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
}