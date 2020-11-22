package io.github.loggerworld.mapper.geography

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.domain.geography.LocationTypeDescription
import io.github.loggerworld.dto.inner.LocationTypeDescriptionInner
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class LocationTypeDescriptionInnerMapper : Mapper<LocationTypeDescriptionInner, LocationTypeDescription> {

    override fun from(source: LocationTypeDescription): LocationTypeDescriptionInner {

        return LocationTypeDescriptionInner(
            LocationTypes.getById(source.locationType.id!!),
            Languages.getById(source.language.id!!),
            source.name,
            source.description
        )
    }
}