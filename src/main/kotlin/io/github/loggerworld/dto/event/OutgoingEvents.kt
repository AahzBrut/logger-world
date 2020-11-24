package io.github.loggerworld.dto.event

import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.dto.response.character.ShortPlayerResponse

sealed class OutgoingEvent : Pool.Poolable

data class LocationChangedEvent(

    var locationId: Short = -1,
    var players: MutableList<ShortPlayerResponse> = mutableListOf(),
    var mobs: MutableList<Long> = mutableListOf(),

) : OutgoingEvent() {

    override fun reset() {
        locationId = -1
        players.clear()
        mobs.clear()
    }
}