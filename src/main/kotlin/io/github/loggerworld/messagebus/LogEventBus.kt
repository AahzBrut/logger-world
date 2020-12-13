package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import io.github.loggerworld.messagebus.event.LogEvent
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class LogEventBus<T : LogEvent> {

    private val eventQueue = ConcurrentLinkedDeque<T>()
    private val eventPools: MutableMap<KClass<*>, Pool<T>> = mutableMapOf()

    fun pushEvent(event: T) {
        eventQueue.push(event)
    }

    fun popEvent(): T {

        return eventQueue.pollLast()
    }

    fun isQueueEmpty(): Boolean {
        return eventQueue.isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    fun newEvent(clazz: KClass<*>): T {

        if (!eventPools.containsKey(clazz)) {
            eventPools[clazz] = Pools.get(clazz.java) as Pool<T>
        }

        return eventPools[clazz]!!.obtain()
    }

    fun destroyEvent(event: T) {
        eventPools[event::class]!!.free(event)
    }
}