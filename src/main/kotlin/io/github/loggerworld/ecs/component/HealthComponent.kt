package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class HealthComponent : Component, Pool.Poolable {

    var maxHealth: Float = 0f
    var health: Float  = 0f
    var defence: Float = 0f

    override fun reset() = Unit

    companion object {
        val mapper = mapperFor<HealthComponent>()
    }
}