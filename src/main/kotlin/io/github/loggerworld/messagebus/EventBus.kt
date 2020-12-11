package io.github.loggerworld.messagebus

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class EventBus<T : Any>(type: KClass<T>){

    private val eventQueue = ConcurrentLinkedDeque<T>()
    private val eventPool: Pool<T> = Pools.get(type.java)

    fun dispatchEvent(setEventParams: (T) -> Unit){
        val event = eventPool.obtain()
        setEventParams(event)
        eventQueue.push(event)
    }

    fun receiveEvent(consumeEvent: (T) -> Unit) : Boolean {
        if (eventQueue.isEmpty()) return false

        val event = eventQueue.poll()
        consumeEvent(event)
        eventPool.free(event)
        return true
    }
}