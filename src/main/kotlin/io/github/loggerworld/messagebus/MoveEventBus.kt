package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pools
import io.github.loggerworld.dto.event.PlayerMoveEvent
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedDeque

@Component
class MoveEventBus {

    private val startGameQueue = ConcurrentLinkedDeque<PlayerMoveEvent>()
    private val eventPool = Pools.get(PlayerMoveEvent::class.java)

    fun pushEvent(event: PlayerMoveEvent) {
        startGameQueue.push(event)
    }

    fun popEvent(): PlayerMoveEvent {

        return startGameQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return startGameQueue.isEmpty()
    }

    fun createEvent(userId: Long, playerId: Long, locationId: Short): PlayerMoveEvent {
        val event = eventPool.obtain()
        event.playerId = playerId
        event.locationId = locationId
        return event
    }

    fun destroyEvent(event: PlayerMoveEvent) {
        eventPool.free(event)
    }
}