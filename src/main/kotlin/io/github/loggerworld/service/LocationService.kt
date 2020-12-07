package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.dto.inner.WorldMap
import io.github.loggerworld.dto.response.geography.LocationResponse
import io.github.loggerworld.dto.response.geography.LocationTypeResponse
import io.github.loggerworld.dto.response.geography.LocationTypesResponse
import io.github.loggerworld.dto.response.geography.LocationsResponse
import io.github.loggerworld.dto.response.monster.MobNestResponse
import io.github.loggerworld.service.domain.LocationDomainService
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

typealias LocationDescriptionsMap = Map<Short, Map<Languages, Pair<String, String>>>
typealias LocationTypeDescriptionsMap = Map<LocationTypes, Map<Languages, Pair<String, String>>>

@Service
class LocationService(
    private val locationDomainService: LocationDomainService,
) {

    private lateinit var worldMap: WorldMap
    private lateinit var locationDescriptionsMap: LocationDescriptionsMap
    private lateinit var locationsTypeDescriptionsMap: LocationTypeDescriptionsMap

    @PostConstruct
    fun initCache(){
        locationDescriptionsMap = initAllLocationDescriptions()
        locationsTypeDescriptionsMap = initAllLocationTypeDescriptions()
        worldMap = initWorldMap()
    }


    fun getLocationTypes(userLanguage: Languages): LocationTypesResponse {

        return LocationTypesResponse(
            getAllLocationTypeDescriptions().entries.map {
                LocationTypeResponse(
                    it.key.ordinal.toByte(),
                    (it.value[userLanguage]
                        ?: error("Language $userLanguage not found in location types cache")).first,
                    (it.value[userLanguage]
                        ?: error("Language $userLanguage not found in location types cache")).second
                )
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
                        ?: error("There is no language: $userLanguage in location descriptions cache.")).second,
                    it.value.monsterNests.map { nestData ->
                        MobNestResponse(
                            nestData.id,
                            nestData.monsterClass,
                            nestData.level,
                            nestData.amount
                        )
                    }
                )
            }.toList()
        )
    }

    fun getWorldMap(): WorldMap {
        return worldMap
    }

    private fun initWorldMap(): WorldMap {
        val allLocations = locationDomainService.getAllLocations()
        val locationDescriptions = getAllLocationDescriptions()
        val locationTypeDescriptions = getAllLocationTypeDescriptions()

        allLocations.forEach { mapEntry ->
            mapEntry.value.descriptions = locationDescriptions[mapEntry.key]
                ?: error("There is no descriptions of location with id: ${mapEntry.key}")
            mapEntry.value.typeDescriptions = locationTypeDescriptions[mapEntry.value.type]
                ?: error("There is no type descriptions of location with id: ${mapEntry.key}")
        }

        allLocations.forEach { location ->
            location.value.neighborLocations = allLocations.filter {
                (it.value.xCoord == (location.value.xCoord - 1).toByte() && it.value.yCoord == location.value.yCoord) ||
                    (it.value.xCoord == (location.value.xCoord + 1).toByte() && it.value.yCoord == location.value.yCoord) ||
                    (it.value.xCoord == location.value.xCoord && it.value.yCoord == (location.value.yCoord - 1).toByte()) ||
                    (it.value.xCoord == location.value.xCoord && it.value.yCoord == (location.value.yCoord + 1).toByte())
            }
        }

        return WorldMap(allLocations)
    }

    fun getAllLocationDescriptions() =
        locationDescriptionsMap

    private fun initAllLocationDescriptions() =
        locationDomainService.getAllLocationDescriptions()

    fun getAllLocationTypeDescriptions() =
        locationsTypeDescriptionsMap

    private fun initAllLocationTypeDescriptions() =
        locationDomainService.getAllLocationTypeDescriptions()
}