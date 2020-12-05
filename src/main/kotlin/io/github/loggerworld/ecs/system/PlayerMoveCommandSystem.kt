package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.EngineSystems.PLAYER_MOVE_COMMAND_SYSTEM
import io.github.loggerworld.ecs.component.MoveStateComponent
import io.github.loggerworld.ecs.component.MoveStates
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.ecs.component.PlayerMoveComponent
import io.github.loggerworld.ecs.component.PositionComponent
import io.github.loggerworld.messagebus.CommandEventBus
import io.github.loggerworld.messagebus.NotificationEventBus
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.WrongCommandEvent
import io.github.loggerworld.service.LocationService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class PlayerMoveCommandSystem(
    private val moveCommandBus: CommandEventBus<PlayerMoveCommand>,
    private val wrongCommandEventEventBus: NotificationEventBus<WrongCommandEvent>,
    private val locationService: LocationService,
) : EntitySystem(PLAYER_MOVE_COMMAND_SYSTEM.ordinal), LogAware {

    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {
        while (!moveCommandBus.isQueueEmpty()) {

            val moveCommand = moveCommandBus.popEvent()
            val player = playerMap[moveCommand.playerId]
            val positionComponent = player[PositionComponent.mapper]!!

            logger().debug("\nPlayer with id: ${moveCommand.playerId} received command to move to location with id:${moveCommand.locationId}")

            if (!locationService
                    .getWorldMap()
                    .locations[positionComponent.locationId]!!.neighborLocations.containsKey(moveCommand.locationId)
            ) {
                val command = wrongCommandEventEventBus.newEvent().also {
                    it.playerId = moveCommand.playerId
                    it.message =
                        "\nLocation with id:${moveCommand.locationId} is not reachable from location with id:${positionComponent.locationId}"
                }
                logger().debug(command.message)
                wrongCommandEventEventBus.pushEvent(command)
            } else {

                player.addComponent<PlayerMoveComponent>(engine) {
                    fromLocationId = positionComponent.locationId
                    currentLocationId = positionComponent.locationId
                    toLocationId = moveCommand.locationId
                    timeToArrive = 5f
                }
                logger().debug("\nMove command started successfully $moveCommand")
                player[MoveStateComponent.mapper]!!.state = MoveStates.DEPARTING
            }
            moveCommandBus.destroyEvent(moveCommand)
        }
    }
}