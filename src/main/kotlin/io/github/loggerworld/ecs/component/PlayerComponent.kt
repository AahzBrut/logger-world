package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.PlayerClasses
import ktx.ashley.mapperFor

class PlayerComponent : Component, Pool.Poolable{

    var userId: Long = -1
    var playerId: Long = -1
    var playerName: String = ""
    var classId: Byte = -1
    var level: Byte = -1

    override fun reset() {
        playerId = -1
        userId = -1
        playerName = ""
        classId = -1
        level = -1
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}