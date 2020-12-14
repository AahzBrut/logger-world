package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.util.emptyEntity
import ktx.ashley.mapperFor
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf

class MonsterComponent : Component, Pool.Poolable {

    var id: Long = -1
    var level: Byte = -1
    var health: Double = .0
    var attack: Double = .0
    var defence: Double = .0
    var monsterClass: MonsterClasses = MonsterClasses.GREY_RAT
    var monsterType: MonsterTypes = MonsterTypes.NORMAL
    val enemies: GdxSet<Entity> = gdxSetOf()
    var target: Entity? = null
    var state: States = States.IDLE
    var nest: Entity = emptyEntity
    var location: Entity = emptyEntity

    override
    fun reset() {
        id = -1
        level = -1
        health = .0
        attack = .0
        defence = .0
        monsterClass = MonsterClasses.GREY_RAT
        monsterType = MonsterTypes.NORMAL
        enemies.clear()
        target = null
        state = States.IDLE
        nest = emptyEntity
        location = emptyEntity
    }

    companion object {
        val mapper = mapperFor<MonsterComponent>()
    }
}