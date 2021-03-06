package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.ecs.EngineSystems.MOVE_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMoveComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class MoveSystem(
    private val logEventBus: LogEventBus<LogEvent>
) : IteratingSystem(allOf(PlayerMoveComponent::class).get(), MOVE_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val moveComponent = entity[PlayerMoveComponent.mapper]!!
        val playerComponent = entity[PlayerComponent.mapper]!!

        if (moveComponent.currentLocationId == moveComponent.fromLocationId) {

            playerComponent.location.addComponent<LocationUpdatedComponent>(engine)
            logger().debug("\nPlayer with id:${playerComponent.playerId} is departing from location with id:${moveComponent.fromLocationId}")

            moveComponent.currentLocationId =
                locationMap[LocationTypes.IN_TRANSIT.ordinal.toShort()].entity[LocationComponent.mapper]!!.locationId
            playerComponent.location = locationMap[LocationTypes.IN_TRANSIT.ordinal.toShort()].entity
            logDepartureEvent(playerComponent.playerId, moveComponent)
        } else {
            moveComponent.timeToArrive -= deltaTime
            if (moveComponent.timeToArrive <= 0) {
                val nextLocation = locationMap[moveComponent.toLocationId].entity[LocationComponent.mapper]!!
                nextLocation.players.add(entity)
                playerComponent.location = locationMap[moveComponent.toLocationId].entity
                entity[StateComponent.mapper]!!.state = States.ARRIVING
                logger().debug("\nPlayer with id:${playerComponent.playerId} is arriving to location with id:${moveComponent.toLocationId}")
                logArrivalEvent(playerComponent.playerId, moveComponent)
                entity.remove(PlayerMoveComponent::class.java)
                playerComponent.location.addComponent<LocationUpdatedComponent>(engine)
            }
        }
    }

    private fun logArrivalEvent(playerId: Long, moveComponent: PlayerMoveComponent) {
        val event = logEventBus.newEvent(ArrivalEvent::class) as ArrivalEvent
        event.playerId = playerId
        event.fromLocationId = moveComponent.fromLocationId
        event.toLocationId = moveComponent.toLocationId
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun logDepartureEvent(playerId: Long, moveComponent: PlayerMoveComponent) {
        val event = logEventBus.newEvent(DepartureEvent::class) as DepartureEvent
        event.playerId = playerId
        event.fromLocationId = moveComponent.fromLocationId
        event.toLocationId = moveComponent.toLocationId
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }
}