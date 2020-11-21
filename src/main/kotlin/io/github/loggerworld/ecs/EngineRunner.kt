package io.github.loggerworld.ecs

import com.badlogic.ashley.core.Engine
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class EngineRunner(
    private val engine: Engine
) {

    private var firstRun = true
    private var timeCount = 0L

    @Scheduled(fixedDelay = 20, initialDelay = 500)
    fun doWorldStep() {
        val currentTime = System.currentTimeMillis()
        firstTimeRunner(currentTime)
        engine.update((currentTime - timeCount) * .001f)
        timeCount = System.currentTimeMillis()
    }

    private fun firstTimeRunner(currentTime: Long) {
        if (firstRun) {
            timeCount = currentTime
            firstRun = false
        }
    }
}