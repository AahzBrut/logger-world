package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class CombatComponent : Component, Pool.Poolable {

    var baseAttackCooldown: Float = .0f
    var attackCooldown: Float = .0f
    var damage: Float = .0f

    override fun reset() {
        baseAttackCooldown = .0f
        attackCooldown = .0f
        damage = .0f
    }

    companion object {
        val mapper = mapperFor<CombatComponent>()
    }
}