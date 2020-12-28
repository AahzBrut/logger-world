package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf

class CombatComponent : Component, Pool.Poolable {

    var baseAttackCooldown: Float = .0f
    var attackCooldown: Float = .0f
    var damage: Float = .0f
    val enemies: GdxSet<Entity> = gdxSetOf()
    var target: Entity? = null
    var locationId: Short = -1
    var damageCounters: MutableMap<Entity, Float> = mutableMapOf()

    override fun reset() {
        baseAttackCooldown = .0f
        attackCooldown = .0f
        damage = .0f
        enemies.clear()
        target = null
        locationId = -1
        damageCounters.clear()
    }

    companion object {
        val mapper = mapperFor<CombatComponent>()
    }
}