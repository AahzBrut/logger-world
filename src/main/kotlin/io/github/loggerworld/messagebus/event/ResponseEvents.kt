package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import io.github.loggerworld.dto.response.monster.MobNestResponse

sealed class NotificationEvent : Pool.Poolable

data class LocationChangedEvent(

    var locationId: Short = -1,
    var players: MutableList<ShortPlayerResponse> = mutableListOf(),
    var mobs: MutableList<Long> = mutableListOf(),
    var mobNests: MutableList<MobNestResponse> = mutableListOf(),

) : NotificationEvent() {

    override fun reset() {
        locationId = -1
        players.clear()
        mobs.clear()
        mobNests.clear()
    }
}

data class WrongCommandEvent(
    var playerId: Long = -1,
    var message: String = ""
): NotificationEvent() {

    override fun reset() {
        playerId = -1
        message = ""
    }
}