package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf

class InventoryComponent : Component, Pool.Poolable {

    var maxSize: Short = 30
    var currentSize: Short = 0
    val slots: GdxArray<Entity> = gdxArrayOf(initialCapacity = 30)

    override fun reset() {
        maxSize = 30
        currentSize = 0
        slots.clear()
    }

    companion object {
        val mapper = mapperFor<InventoryComponent>()
    }
}