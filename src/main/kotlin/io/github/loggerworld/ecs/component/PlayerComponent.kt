package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.util.emptyEntity
import ktx.ashley.mapperFor
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf

class PlayerComponent : Component, Pool.Poolable{

    var userId: Long = -1
    var playerId: Long = -1
    var playerName: String = ""
    var classId: Byte = -1
    var level: Byte = -1
    val enemies: GdxSet<Entity> = gdxSetOf()
    var target: Entity? = null
    var stats: Map<PlayerStatEnum, Double> = mutableMapOf()
    var location: Entity = emptyEntity

    override fun reset() {
        playerId = -1
        userId = -1
        playerName = ""
        classId = -1
        level = -1
        target = null
        enemies.clear()
        stats = mutableMapOf()
        location = emptyEntity
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}