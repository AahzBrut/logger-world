package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import io.github.loggerworld.messagebus.event.CommandEvent
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class CommandEventBus<T : CommandEvent>(type: KClass<T>) {

    private val eventQueue = ConcurrentLinkedDeque<T>()
    private val eventPool: Pool<T> = Pools.get(type.java)

    fun pushEvent(event: T) {
        eventQueue.push(event)
    }

    fun popEvent(): T {

        return eventQueue.pop()
    }

    fun isQueueEmpty(): Boolean {
        return eventQueue.isEmpty()
    }

    fun newEvent(): T {
        return eventPool.obtain()
    }

    fun destroyEvent(event: T) {
        eventPool.free(event)
    }
}