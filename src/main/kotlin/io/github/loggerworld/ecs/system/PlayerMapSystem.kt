package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems.PLAYER_MAP_SYSTEM
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.collections.set
import org.springframework.stereotype.Component


@Component
class PlayerMapSystem : IteratingSystem(allOf(PlayerComponent::class).get(), PLAYER_MAP_SYSTEM.ordinal), EntityListener, LogAware {

    private lateinit var playerMapEntity: Entity


    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        playerMapEntity = engine.entity {
            with<PlayerMapComponent> {}
        }
        engine.addEntityListener(allOf(PlayerComponent::class).get(),this)
        logger().debug("PlayerMapSystem added to engine.")
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
        engine.removeEntity(playerMapEntity)
        logger().debug("\nLocationMapSystem removed from engine.")
    }

    override fun entityAdded(entity: Entity) {
        val playerMap = playerMapEntity[PlayerMapComponent.mapper]!!
        val playerComponent = entity[PlayerComponent.mapper]!!
        playerMap.playerMap[playerComponent.playerId] = entity
        logger().debug("\nPlayer with id: ${playerComponent.playerId} added to the player map")
    }

    override fun entityRemoved(entity: Entity) {
        val playerMap = playerMapEntity[PlayerMapComponent.mapper]!!
        val playerComponent = entity[PlayerComponent.mapper]!!
        playerMap.playerMap.remove(playerComponent.playerId)
        logger().debug("\nPlayer with id: ${playerComponent.playerId} added to the player map")
    }

    override fun processEntity(entity: Entity, deltaTime: Float) = Unit
}