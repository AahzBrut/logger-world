package io.github.loggerworld.dto.inner

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.dto.inner.monster.MonsterNestData

data class WorldLocation(
    var id: Short = Short.MIN_VALUE,
    var xCoord: Byte = Byte.MIN_VALUE,
    var yCoord: Byte = Byte.MIN_VALUE,
    var descriptions: Map<Languages, Pair<String, String>> = emptyMap(),
    var type: LocationTypes,
    var typeDescriptions: Map<Languages, Pair<String, String>> = emptyMap(),
    var neighborLocations: Map<Short, WorldLocation> = emptyMap(),
    var innerLocations: Map<Short, WorldLocation> = emptyMap(),
    var monsterNests: List<MonsterNestData> = emptyList(),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorldLocation) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.toInt()
    }
}
