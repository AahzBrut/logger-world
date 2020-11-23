package io.github.loggerworld.dto.event

import com.badlogic.gdx.utils.Pool

sealed class IncomingEvent : Pool.Poolable

data class PlayerStartEvent(
    var playerId: Long,
    var locationId: Long
) : IncomingEvent() {

    override fun reset() {
        playerId = -1
        locationId = -1
    }
}