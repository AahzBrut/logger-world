package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.collections.gdxMapOf

class PlayerMapComponent : Component, Pool.Poolable {

    val playerMap = gdxMapOf<Long, Entity>()

    override fun reset() {
        playerMap.clear()
    }

    companion object{
        val mapper = mapperFor<PlayerMapComponent>()
    }
}