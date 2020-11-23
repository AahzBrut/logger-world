package io.github.loggerworld.dto

import com.badlogic.ashley.core.Entity

data class LocationInhabitantsUpdated(
    var entity: Entity,
    var updated: Boolean
)
