package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.EngineSystems.PLAYER_MOVE_COMMAND_SYSTEM
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.ecs.component.PlayerMoveComponent
import io.github.loggerworld.messagebus.CommandEventBus
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class PlayerMoveCommandSystem(
    private val moveEventBus: CommandEventBus<PlayerMoveCommand>,
) : EntitySystem(PLAYER_MOVE_COMMAND_SYSTEM.ordinal), LogAware {

    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }


    override fun update(deltaTime: Float) {
        while (!moveEventBus.isQueueEmpty()) {

            val moveEvent = moveEventBus.popEvent()
            val player = playerMap[moveEvent.playerId]

            player.add(PlayerMoveComponent().also {
                it.locationId=moveEvent.locationId
            })

            moveEventBus.destroyEvent(moveEvent)
        }
    }
}