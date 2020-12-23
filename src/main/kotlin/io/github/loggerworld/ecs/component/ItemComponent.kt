package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum
import ktx.ashley.mapperFor
import ktx.collections.GdxMap
import ktx.collections.gdxMapOf

class ItemComponent : Component, Pool.Poolable {

    var id: Long = 0
    var category: ItemCategories = ItemCategories.NOTHING
    var quality: ItemQualities = ItemQualities.NONE
    var quantity: Long = 0
    val stats: MutableMap<ItemStatEnum, Float> = mutableMapOf()
    var stackable: Boolean = false

    override fun reset() {
        id = 0
        category = ItemCategories.NOTHING
        quality = ItemQualities.NONE
        quantity = 0
        stats.clear()
        stackable = false
    }

    companion object {
        val mapper = mapperFor<ItemComponent>()
    }
}