package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.ecs.EngineSystems.MOVE_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MoveStateComponent
import io.github.loggerworld.ecs.component.MoveStates
import io.github.loggerworld.ecs.component.PlayerMoveComponent
import io.github.loggerworld.ecs.component.PositionComponent
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class MoveSystem : IteratingSystem(allOf(PlayerMoveComponent::class).get(), MOVE_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val moveComponent = entity[PlayerMoveComponent.mapper]!!
        val positionComponent = entity[PositionComponent.mapper]!!

        if (moveComponent.currentLocationId == moveComponent.fromLocationId) {
            locationMap[moveComponent.currentLocationId].updated = true
            moveComponent.currentLocationId =
                locationMap[LocationTypes.IN_TRANSIT.ordinal.toShort()].entity[LocationComponent.mapper]!!.locationId
            positionComponent.locationId = LocationTypes.IN_TRANSIT.ordinal.toShort()
        } else {
            moveComponent.timeToArrive -= deltaTime
            if (moveComponent.timeToArrive <= 0) {
                locationMap[moveComponent.toLocationId].updated = true
                val nextLocation = locationMap[moveComponent.toLocationId].entity[LocationComponent.mapper]!!
                nextLocation.players.add(entity)
                positionComponent.locationId = moveComponent.toLocationId
                entity[MoveStateComponent.mapper]!!.state = MoveStates.ARRIVING
                entity.remove(PlayerMoveComponent::class.java)
            }
        }
    }
}