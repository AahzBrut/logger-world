package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.EngineSystems.PLAYER_SPAWN_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MoveStateComponent
import io.github.loggerworld.ecs.component.MoveStates
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PositionComponent
import io.github.loggerworld.messagebus.CommandEventBus
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import org.springframework.stereotype.Service

@Service
class PlayerSpawnSystem(
    private val startEventBus: CommandEventBus<PlayerStartCommand>,
) : EntitySystem(PLAYER_SPAWN_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun update(deltaTime: Float) {
        while (!startEventBus.isQueueEmpty()) {

            val event = startEventBus.popEvent()

            logger().debug("\nPlayer with id:${event.playerId} spawned into location with id:${event.locationId}")
            val player = spawnPlayer(event)
            addPlayerToTargetLocation(player, event)
            locationMap[event.locationId].updated = true

            startEventBus.destroyEvent(event)
        }
    }

    private fun addPlayerToTargetLocation(player: Entity, command: PlayerStartCommand) {
        val locationEntity = locationMap[command.locationId].entity
        val locationComponent = locationEntity[LocationComponent.mapper]!!
        locationComponent.players.add(player)
    }

    private fun spawnPlayer(command: PlayerStartCommand): Entity {
        return engine.entity {
            with<PositionComponent> {
                locationId = command.locationId
            }
            with<PlayerComponent> {
                playerId = command.playerId
                userId = command.userId
                playerName = command.name
                classId = command.classId
                level = command.level
            }
            with<MoveStateComponent> { state = MoveStates.ARRIVING }
        }
    }
}