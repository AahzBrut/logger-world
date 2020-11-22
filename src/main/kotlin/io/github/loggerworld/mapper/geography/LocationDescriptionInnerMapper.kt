package io.github.loggerworld.mapper.geography

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.geography.LocationDescription
import io.github.loggerworld.dto.inner.LocationDescriptionInner
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class LocationDescriptionInnerMapper : Mapper<LocationDescriptionInner, LocationDescription> {

    override fun from(source: LocationDescription) =
        LocationDescriptionInner(
            source.location.id!!,
            Languages.getById(source.language.id!!),
            source.name,
            source.description
        )
}