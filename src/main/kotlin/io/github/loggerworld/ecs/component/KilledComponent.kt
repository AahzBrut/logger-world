package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.util.emptyEntity
import ktx.ashley.mapperFor

class KilledComponent : Component, Pool.Poolable {

    var killer: Entity = emptyEntity
    var locationId: Short = -1

    override fun reset() = Unit

    companion object {
        val mapper = mapperFor<KilledComponent>()
    }
}