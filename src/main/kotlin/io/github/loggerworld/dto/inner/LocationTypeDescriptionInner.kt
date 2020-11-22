package io.github.loggerworld.dto.inner

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LocationTypes

data class LocationTypeDescriptionInner(
    var locationType: LocationTypes,
    var language: Languages,
    var name: String,
    var description: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocationTypeDescriptionInner) return false

        if (locationType != other.locationType) return false

        return true
    }

    override fun hashCode(): Int {
        return locationType.hashCode()
    }
}