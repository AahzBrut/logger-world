package io.github.loggerworld.messagebus

import io.github.loggerworld.dto.event.LocationArriveEvent
import io.github.loggerworld.dto.event.StartGameEvent
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedDeque

@Component
class OutGoingEventBus {
    private val startGameQueue = ConcurrentLinkedDeque<LocationArriveEvent>()

    fun pushEvent(event: LocationArriveEvent) {
        startGameQueue.push(event)
    }

    fun popEvent(): LocationArriveEvent {

        return startGameQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return startGameQueue.isEmpty()
    }

    fun getQueueSize(): Int {

        return startGameQueue.size
    }
}