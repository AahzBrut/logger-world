package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Component

@Component
class LocationInhabitantAlertSystem : EntitySystem(3), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun update(deltaTime: Float) {
        locationMap
            .forEach {
                if (it.value.updated) {
                    logger().info("Inhabitants changed in location with id: ${it.key}")
                    it.value.updated = false
                }
            }
    }
}