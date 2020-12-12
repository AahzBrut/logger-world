package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.RemoveComponent
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import org.springframework.stereotype.Service

@Service
class RemoveSystem : IteratingSystem(allOf(RemoveComponent::class).get(), EngineSystems.REMOVE_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        engine.removeEntity(entity)
    }
}