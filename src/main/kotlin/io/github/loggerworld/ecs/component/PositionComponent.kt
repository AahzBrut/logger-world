package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PositionComponent : Component, Pool.Poolable {

    var locationId: Short = -1

    override fun reset() {
        locationId = -1
    }
}