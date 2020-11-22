package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service


@Service
class CombatSystem: IteratingSystem(allOf(LocationComponent::class).get()), LogAware {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val locationComponent = entity[LocationComponent.mapper]!!

        //logger().info("${locationComponent.locationType}: ${locationComponent.locationId}-(${locationComponent.xCoord},${locationComponent.yCoord})")
    }
}