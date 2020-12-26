package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class EquipmentChangeComponent : Component, Pool.Poolable {

    override fun reset() = Unit

    companion object {
        val mapper = mapperFor<EquipmentChangeComponent>()
    }
}