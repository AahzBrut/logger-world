package io.github.loggerworld.ecs

import com.badlogic.ashley.core.Engine
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class EngineRunner(
    private val engine: Engine,
    private val performanceCounter: PerformanceCounter
) {

    private var firstRun = true
    private var timeCount = 0L

    @Scheduled(fixedDelayString = "\${worldRefreshRate}", initialDelay = 500)
    fun doWorldStep() {
        performanceCounter.start(PerfCounters.ECS_ENGINE)
        val currentTime = System.currentTimeMillis()
        firstTimeRunner(currentTime)
        engine.update((currentTime - timeCount) * .001f)
        timeCount = System.currentTimeMillis()
        performanceCounter.stop(PerfCounters.ECS_ENGINE)
    }

    private fun firstTimeRunner(currentTime: Long) {
        if (firstRun) {
            timeCount = currentTime
            firstRun = false
        }
    }
}