package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import org.springframework.stereotype.Component

@Component
class MovementSystem : IteratingSystem(allOf(PlayerComponent::class).get()), LogAware {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        logger().info("In MovementSystem, deltaTime: $deltaTime")
    }
}