package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.dto.event.StartGameEvent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PositionComponent
import io.github.loggerworld.messagebus.IncomingEventBus
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import org.springframework.stereotype.Service

@Service
class PlayerSpawnSystem(
    private val incomingEventBus: IncomingEventBus,
) : EntitySystem(2), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun update(deltaTime: Float) {
        while (!incomingEventBus.isQueueEmpty()) {

            val event = incomingEventBus.popEvent()

            spawnPlayer(event)
            locationMap[event.locationId].updated = true

            logger().info("Player with id: ${event.playerId} is spawned to location ${event.locationId}")
        }
    }

    private fun spawnPlayer(event: StartGameEvent) {
        engine.entity {
            with<PositionComponent> {
                locationId = event.locationId
            }
            with<PlayerComponent> {
                playerId = event.playerId
            }
        }
    }
}