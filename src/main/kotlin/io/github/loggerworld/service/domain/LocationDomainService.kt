package io.github.loggerworld.service.domain

import io.github.loggerworld.dto.inner.WorldLocation
import io.github.loggerworld.mapper.geography.LocationDescriptionInnerMapper
import io.github.loggerworld.mapper.geography.LocationTypeDescriptionInnerMapper
import io.github.loggerworld.mapper.geography.WorldLocationMapper
import io.github.loggerworld.repository.geography.LocationDescriptionRepository
import io.github.loggerworld.repository.geography.LocationRepository
import io.github.loggerworld.repository.geography.LocationTypeDescriptionRepository
import io.github.loggerworld.service.LocationDescriptionsMap
import io.github.loggerworld.service.LocationTypeDescriptionsMap
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class LocationDomainService(
    private val locationRepository: LocationRepository,
    private val locationDescriptionRepository: LocationDescriptionRepository,
    private val locationTypeDescriptionRepository: LocationTypeDescriptionRepository,
    private val worldLocationMapper: WorldLocationMapper,
    private val locationDescriptionInnerMapper: LocationDescriptionInnerMapper,
    private val locationTypeDescriptionInnerMapper: LocationTypeDescriptionInnerMapper
) {

    @Transactional
    fun getAllLocations(): Map<Short, WorldLocation> {
        val locations = locationRepository.findAll()

        return worldLocationMapper.from(locations).map { it.id to it }.toMap()
    }

    @Transactional
    fun getAllLocationDescriptions(): LocationDescriptionsMap {
        val allDescriptions = locationDescriptionRepository.findAll()

        return locationDescriptionInnerMapper.from(allDescriptions).groupBy {
            it.locationId
        }.mapValues { entry ->
            entry.value.associate {
                it.language to Pair(it.name, it.description)
            }
        }
    }

    fun getAllLocationTypeDescriptions(): LocationTypeDescriptionsMap {
        val allDescriptions = locationTypeDescriptionRepository.findAll()

        return locationTypeDescriptionInnerMapper
            .from(allDescriptions)
            .groupBy { it.locationType }
            .mapValues { entry ->
                entry.value.associate {
                    it.language to Pair(it.name, it.description)
                }
            }
    }
}