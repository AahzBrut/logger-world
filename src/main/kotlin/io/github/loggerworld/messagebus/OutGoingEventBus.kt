package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pools
import io.github.loggerworld.dto.event.LocationChangedEvent
import io.github.loggerworld.dto.event.PlayerStartEvent
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedDeque

@Component
class OutGoingEventBus {

    private val startGameQueue = ConcurrentLinkedDeque<LocationChangedEvent>()
    private val eventPool = Pools.get(LocationChangedEvent::class.java)

    fun pushEvent(event: LocationChangedEvent) {
        startGameQueue.push(event)
    }

    fun popEvent(): LocationChangedEvent {

        return startGameQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return startGameQueue.isEmpty()
    }

    fun getQueueSize(): Int {

        return startGameQueue.size
    }

    fun createEvent(locationId: Short, players: List<ShortPlayerResponse>): LocationChangedEvent {
        val event = eventPool.obtain()
        event.locationId = locationId
        event.players.addAll(players)
        return event
    }

    fun destroyEvent(event: LocationChangedEvent) {
        eventPool.free(event)
    }

}