package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.dto.response.character.ShortPlayerResponse

sealed class NotificationEvent : Pool.Poolable

data class LocationChangedEvent(

    var locationId: Short = -1,
    var players: MutableList<ShortPlayerResponse> = mutableListOf(),
    var mobs: MutableList<Long> = mutableListOf(),

) : NotificationEvent() {

    override fun reset() {
        locationId = -1
        players.clear()
        mobs.clear()
    }
}