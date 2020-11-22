package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.dto.event.LocationArriveEvent
import io.github.loggerworld.ecs.WorldCache
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PositionComponent
import io.github.loggerworld.messagebus.IncomingEventBus
import io.github.loggerworld.messagebus.OutGoingEventBus
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.entity
import ktx.ashley.with
import org.springframework.stereotype.Service

@Service
class PlayerSpawnSystem(
    private val worldCache: WorldCache,
    private val incomingEventBus: IncomingEventBus,
    private val outGoingEventBus: OutGoingEventBus,
) : EntitySystem(), LogAware {

    override fun update(deltaTime: Float) {
        while (!incomingEventBus.isQueueEmpty()) {

            val event = incomingEventBus.popEvent()

            engine.entity {
                with<PositionComponent> {
                    locationId = event.locationId
                }
                with<PlayerComponent> {
                    playerId = event.playerId
                }
            }
            outGoingEventBus.pushEvent(LocationArriveEvent(event.playerId, event.locationId))

            logger().info("Player with id: ${event.playerId} is spawned to location ${event.locationId}")
        }
    }
}