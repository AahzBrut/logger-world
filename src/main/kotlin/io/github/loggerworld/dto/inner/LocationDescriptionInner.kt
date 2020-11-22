package io.github.loggerworld.dto.inner

import io.github.loggerworld.domain.enums.Languages

data class LocationDescriptionInner(
    var locationId: Short,
    var language: Languages,
    var name: String,
    var description: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocationDescriptionInner) return false

        if (locationId != other.locationId) return false

        return true
    }

    override fun hashCode(): Int {
        return locationId.toInt()
    }
}