package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class InventoryComponent : Component, Pool.Poolable {

    var maxSize: Int = 30
    val slots: MutableList<Entity> = mutableListOf()

    override fun reset() = Unit

    companion object {
        val mapper = mapperFor<InventoryComponent>()
    }
}