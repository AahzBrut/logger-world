package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class InventoryComponent : Component, Pool.Poolable {

    var maxSize: Short = 30
    var currentSize: Short = 0
    val slots: MutableList<Entity> = mutableListOf()

    override fun reset() {
        maxSize = 30
        currentSize = 0
        slots.clear()
    }

    companion object {
        val mapper = mapperFor<InventoryComponent>()
    }
}