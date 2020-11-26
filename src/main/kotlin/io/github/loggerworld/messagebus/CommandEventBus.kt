package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import io.github.loggerworld.messagebus.event.CommandEvent
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class CommandEventBus<T : CommandEvent>(type: KClass<T>) {

    private val startGameQueue = ConcurrentLinkedDeque<T>()
    private val eventPool: Pool<T> = Pools.get(type.java)

    fun pushEvent(event: T) {
        startGameQueue.push(event)
    }

    fun popEvent(): T {

        return startGameQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return startGameQueue.isEmpty()
    }

    fun newEvent(): T {
        return eventPool.obtain()
    }

    fun destroyEvent(event: T) {
        eventPool.free(event)
    }
}