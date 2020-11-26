package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.EngineSystems.PLAYER_SPAWN_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
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

            spawnPlayer(event)
            addPlayerToTargetLocation(event)
            locationMap[event.locationId].updated = true

            logger().debug("Player with id: ${event.playerId} is spawned to location ${event.locationId}")
            startEventBus.destroyEvent(event)
        }
    }

    private fun addPlayerToTargetLocation(command: PlayerStartCommand) {
        val locationEntity = locationMap[command.locationId].entity
        val locationComponent = locationEntity[LocationComponent.mapper]!!
        locationComponent.players.add(command.playerId)
    }

    private fun spawnPlayer(command: PlayerStartCommand) {
        engine.entity {
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
        }
    }
}