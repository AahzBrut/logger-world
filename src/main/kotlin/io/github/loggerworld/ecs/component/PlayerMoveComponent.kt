package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PlayerMoveComponent : Component, Pool.Poolable {

    var toLocationId: Short = -1
    var fromLocationId: Short = -1
    var currentLocationId: Short = -1
    var timeToArrive: Float = 0f

    override fun reset() {
        toLocationId = -1
        fromLocationId = -1
        currentLocationId = -1
        timeToArrive = 0f
    }

    companion object{
        val mapper = mapperFor<PlayerMoveComponent>()
    }
}