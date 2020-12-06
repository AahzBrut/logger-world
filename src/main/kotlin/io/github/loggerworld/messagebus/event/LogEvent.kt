package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool

sealed class LogEvent : Pool.Poolable

data class LoginEvent(

    var playerId: Long = 0,
    var locationId: Short = 0

) : LogEvent() {
    override fun reset() {
        playerId = 0
        locationId = 0
    }
}
