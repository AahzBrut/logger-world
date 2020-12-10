package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool

sealed class CommandEvent : Pool.Poolable

data class PlayerStartCommand(
    var userId: Long = -1,
    var playerId: Long = -1,
    var locationId: Short = -1,
    var name: String = "",
    var classId: Byte = -1,
    var level: Byte = -1
) : CommandEvent() {

    override fun reset() {
        userId = -1
        playerId = -1
        locationId = -1
        name=""
        classId=-1
        level=-1
    }
}

data class PlayerMoveCommand(
    var playerId: Long = -1,
    var locationId: Short = -1,
) : CommandEvent() {

    override fun reset() {
        playerId = -1
        locationId = -1
    }
}

data class PlayerKickMonsterNestCommand(
    var playerId: Long = -1,
    var monsterNestId: Short = -1,
) : CommandEvent() {

    override fun reset() {
        playerId = -1
        monsterNestId = -1
    }
}