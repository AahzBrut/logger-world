package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PlayerComponent : Component, Pool.Poolable{

    var playerId: Long = -1

    override fun reset() {
        playerId = -1
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}