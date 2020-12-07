package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool
import java.time.LocalDateTime

sealed class LogEvent : Pool.Poolable

data class LoginEvent(
    var playerId: Long = 0,
    var locationId: Short = 0,
    var created: LocalDateTime = LocalDateTime.now()
) : LogEvent() {
    override fun reset() {
        playerId = 0
        locationId = 0
        created = LocalDateTime.now()
    }
}

data class DepartureEvent(
    var playerId: Long = 0,
    var fromLocationId: Short = 0,
    var toLocationId: Short = 0,
    var created: LocalDateTime = LocalDateTime.now()
) : LogEvent() {
    override fun reset() {
        playerId = 0
        fromLocationId = 0
        toLocationId = 0
        created = LocalDateTime.now()
    }
}

data class ArrivalEvent(
    var playerId: Long = 0,
    var fromLocationId: Short = 0,
    var toLocationId: Short = 0,
    var created: LocalDateTime = LocalDateTime.now()
) : LogEvent() {
    override fun reset() {
        playerId = 0
        fromLocationId = 0
        toLocationId = 0
        created = LocalDateTime.now()
    }
}
