package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.EngineSystems.PLAYER_MOVE_COMMAND_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.ecs.component.PlayerMoveComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.WrongCommandEvent
import io.github.loggerworld.service.LocationService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.hasNot
import org.springframework.stereotype.Service

@Service
class PlayerMoveCommandSystem(
    private val wrongCommandEventEventBus: EventBus<WrongCommandEvent>,
    private val locationService: LocationService,
    private val moveCommandBus: EventBus<PlayerMoveCommand>,
) : EntitySystem(PLAYER_MOVE_COMMAND_SYSTEM.ordinal), LogAware {

    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {
        while (moveCommandBus.receiveEvent { moveCommand ->
                logger().debug("\n\nPlayer with id: ${moveCommand.playerId} received command to move to location with id:${moveCommand.locationId}")

                val player = playerMap[moveCommand.playerId]
                val playerComp = player[PlayerComponent.mapper]!!
                val locationId = playerComp.location[LocationComponent.mapper]!!.locationId

                if (!locationService
                        .getWorldMap()
                        .locations[locationId]!!.neighborLocations.containsKey(moveCommand.locationId)
                ) {
                    wrongCommandEventEventBus.dispatchEvent {
                        it.playerId = moveCommand.playerId
                        it.message =
                            "Location with id:${moveCommand.locationId} is not reachable from location with id:${locationId}"
                        logger().debug("\n\n${it.message}")
                    }
                } else {

                    player.addComponent<PlayerMoveComponent>(engine) {
                        fromLocationId = locationId
                        currentLocationId = locationId
                        toLocationId = moveCommand.locationId
                        timeToArrive = 5f
                    }
                    logger().debug("\nMove command started successfully $moveCommand")
                    player[StateComponent.mapper]!!.state = States.DEPARTING
                }
            }) {
            // Empty body
        }
    }
}