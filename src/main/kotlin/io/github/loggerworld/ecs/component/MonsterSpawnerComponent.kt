package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.util.emptyEntity
import ktx.ashley.mapperFor

class MonsterSpawnerComponent : Component, Pool.Poolable {

    var id: Short = 0
    var location: Entity = emptyEntity
    var monsterClass: MonsterClasses = MonsterClasses.GREY_RAT
    var level: Byte = 0
    var amount: Short = 0
    var minRespawnTimer: Double =.0
    var maxRespawnTimer: Double =.0
    var monsterCounter: Long = 0

    override fun reset() {
        id = 0
        location = emptyEntity
        monsterClass = MonsterClasses.GREY_RAT
        level = 0
        amount = 0
        minRespawnTimer = .0
        maxRespawnTimer = .0
        monsterCounter = 0
    }

    companion object {
        val mapper = mapperFor<MonsterSpawnerComponent>()
    }


}