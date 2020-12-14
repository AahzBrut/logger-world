package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.LogTypes
import java.time.OffsetDateTime

sealed class LogEvent : Pool.Poolable

data class LoginEvent(
    var eventType: LogTypes = LogTypes.LOGIN,
    var playerId: Long = 0,
    var locationId: Short = 0,
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent() {
    override fun reset() {
        playerId = 0
        locationId = 0
        created = OffsetDateTime.now()
    }
}

data class DepartureEvent(
    var eventType: LogTypes = LogTypes.DEPARTURE,
    var playerId: Long = 0,
    var fromLocationId: Short = 0,
    var toLocationId: Short = 0,
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent() {
    override fun reset() {
        playerId = 0
        fromLocationId = 0
        toLocationId = 0
        created = OffsetDateTime.now()
    }
}

data class ArrivalEvent(
    var eventType: LogTypes = LogTypes.ARRIVAL,
    var playerId: Long = 0,
    var fromLocationId: Short = 0,
    var toLocationId: Short = 0,
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent() {
    override fun reset() {
        playerId = 0
        fromLocationId = 0
        toLocationId = 0
        created = OffsetDateTime.now()
    }
}

data class NestKickEvent(
    var eventType: LogTypes = LogTypes.NEST_KICKED,
    var playerId: Long = 0,
    var nestId: Short = 0,
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
        nestId = 0
    }
}

data class AttackedByMobEvent(
    var eventType: LogTypes = LogTypes.ATTACKED_BY_MOB,
    var playerId: Long = 0,
    var monsterName: String = "",
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
    }
}

data class AttackMobEvent(
    var eventType: LogTypes = LogTypes.ATTACK_MOB,
    var playerId: Long = 0,
    var monsterName: String = "",
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
    }
}

data class DealDamageToMobEvent(
    var eventType: LogTypes = LogTypes.DEAL_DAMAGE_MOB,
    var playerId: Long = 0,
    var monsterName: String = "",
    var damage: Int = 0,
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
        damage = 0
    }
}

data class ReceiveDamageFromMobEvent(
    var eventType: LogTypes = LogTypes.RECEIVE_DAMAGE_MOB,
    var playerId: Long = 0,
    var monsterName: String = "",
    var damage: Int = 0,
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
        damage = 0
    }
}

data class PlayerKilledByMobEvent(
    var eventType: LogTypes = LogTypes.PLAYER_KILLED_BY_MOB,
    var playerId: Long = 0,
    var monsterName: String = "",
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
    }
}

data class PlayerKillMobEvent(
    var eventType: LogTypes = LogTypes.MOB_KILLED,
    var playerId: Long = 0,
    var monsterName: String = "",
    var created: OffsetDateTime = OffsetDateTime.now()
) : LogEvent(){
    override fun reset() {
        playerId = 0
    }
}
