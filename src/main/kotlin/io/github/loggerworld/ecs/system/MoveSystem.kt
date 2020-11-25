package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMoveComponent
import io.github.loggerworld.ecs.component.PositionComponent
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class MoveSystem : IteratingSystem(allOf(PlayerMoveComponent::class).get(),5), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val moveComponent = entity[PlayerMoveComponent.mapper]!!
        val positionComponent = entity[PositionComponent.mapper]!!
        val playerComponent = entity[PlayerComponent.mapper]!!
        val currentLocationId = positionComponent.locationId
        val futureLocationId = moveComponent.locationId

        locationMap[currentLocationId].updated = true
        locationMap[futureLocationId].updated = true

        val curLocation = locationMap[currentLocationId].entity[LocationComponent.mapper]!!
        curLocation.players.remove(playerComponent.playerId)

        val nextLocation = locationMap[futureLocationId].entity[LocationComponent.mapper]!!
        nextLocation.players.add(playerComponent.playerId)
        positionComponent.locationId=futureLocationId

        entity.remove(PlayerMoveComponent::class.java)
    }
}