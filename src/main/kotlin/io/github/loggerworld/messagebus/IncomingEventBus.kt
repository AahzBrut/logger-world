package io.github.loggerworld.messagebus

import io.github.loggerworld.dto.event.StartGameEvent
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedDeque

@Component
class IncomingEventBus {

    private val startGameQueue = ConcurrentLinkedDeque<StartGameEvent>()

    fun pushEvent(event: StartGameEvent) {
        startGameQueue.push(event)
    }

    fun popEvent(): StartGameEvent {

        return startGameQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return startGameQueue.isEmpty()
    }

    fun getQueueSize(): Int {

        return startGameQueue.size
    }
}