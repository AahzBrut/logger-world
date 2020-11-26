package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.dto.LocationInhabitantsChanged
import io.github.loggerworld.ecs.EngineSystems.LOCATION_MAP_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.collections.set
import org.springframework.stereotype.Component


@Component
class LocationMapSystem : IteratingSystem(allOf(LocationComponent::class).get(), LOCATION_MAP_SYSTEM.ordinal), EntityListener, LogAware {

    private lateinit var locationMapEntity: Entity

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        locationMapEntity = engine.entity {
            with<LocationMapComponent> {}
        }
        engine.addEntityListener(allOf(LocationComponent::class).get(),this)
        logger().debug("LocationMapSystem added to engine.")
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
        engine.removeEntity(locationMapEntity)
        logger().debug("LocationMapSystem removed from engine.")
    }

    override fun entityAdded(entity: Entity) {
        val locationMapComponent = locationMapEntity[LocationMapComponent.mapper]!!
        val locationComponent = entity[LocationComponent.mapper]!!
        locationMapComponent.locationMap[locationComponent.locationId] = LocationInhabitantsChanged(entity, false)
        logger().debug("Entity with id:${locationComponent.locationId} added to the world.")
    }

    override fun entityRemoved(entity: Entity) {
        val locationMapComponent = locationMapEntity[LocationMapComponent.mapper]!!
        val locationComponent = entity[LocationComponent.mapper]!!
        locationMapComponent.locationMap.remove(locationComponent.locationId)
        logger().debug("Entity with id:${locationComponent.locationId} removed from the world.")
    }

    override fun processEntity(entity: Entity, deltaTime: Float) = Unit
}