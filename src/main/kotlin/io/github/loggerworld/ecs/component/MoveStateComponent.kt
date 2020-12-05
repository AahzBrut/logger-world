package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class MoveStates{
    STATIONARY,
    ARRIVING,
    DEPARTING
}

class MoveStateComponent : Component, Pool.Poolable {

    var state: MoveStates = MoveStates.STATIONARY

    override fun reset() {
        state = MoveStates.STATIONARY
    }

    companion object {
        val mapper = mapperFor<MoveStateComponent>()
    }
}
