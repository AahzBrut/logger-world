package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pools
import io.github.loggerworld.dto.event.PlayerStartEvent
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedDeque

@Component
class IncomingEventBus {

    private val startGameQueue = ConcurrentLinkedDeque<PlayerStartEvent>()
    private val eventPool = Pools.get(PlayerStartEvent::class.java)

    fun pushEvent(event: PlayerStartEvent) {
        startGameQueue.push(event)
    }

    fun popEvent(): PlayerStartEvent {

        return startGameQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return startGameQueue.isEmpty()
    }

    fun getQueueSize(): Int {

        return startGameQueue.size
    }

    fun createEvent(userId: Long, playerId: Long, locationId: Short, name: String, classId: Byte, level: Byte): PlayerStartEvent {
        val event = eventPool.obtain()
        event.userId = userId
        event.playerId = playerId
        event.locationId = locationId
        event.name=name
        event.level=level
        event.classId=classId
        return event
    }

    fun destroyEvent(event: PlayerStartEvent) {
        eventPool.free(event)
    }
}