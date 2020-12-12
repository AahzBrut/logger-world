package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class States{
    IDLE,
    ARRIVING,
    DEPARTING,
    IN_COMBAT,
}

class StateComponent : Component, Pool.Poolable {

    var state: States = States.IDLE

    override fun reset() {
        state = States.IDLE
    }

    companion object {
        val mapper = mapperFor<StateComponent>()
    }
}
