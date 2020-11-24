package io.github.loggerworld.dto.event

import com.badlogic.gdx.utils.Pool

sealed class IncomingEvent : Pool.Poolable

data class PlayerStartEvent(
    var userId: Long = -1,
    var playerId: Long = -1,
    var locationId: Short = -1,
    var name: String = "",
    var classId: Byte = -1,
    var level: Byte = -1
) : IncomingEvent() {

    override fun reset() {
        userId = -1
        playerId = -1
        locationId = -1
        name=""
        classId=-1
        level=-1
    }
}