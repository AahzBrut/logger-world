package io.github.loggerworld.service.perfcount

import io.github.loggerworld.dto.response.perfcounter.PerformanceCounterResponse
import io.github.loggerworld.dto.response.perfcounter.PerformanceCountersResponse
import io.github.loggerworld.util.RingBuffer
import io.github.loggerworld.util.nanoSecond
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PerformanceCounter {

    @Value("\${logger-world.perf-counters.max-count}")
    private lateinit var maxCount: String

    private val counters: MutableMap<PerfCounters, RingBuffer> by lazy {
        PerfCounters.values().associate {
            it to RingBuffer(maxCount.toInt())
        }.toMutableMap()
    }

    private val timeStamps: MutableMap<PerfCounters, Long> by lazy {
        PerfCounters.values().associate {
            it to Long.MIN_VALUE
        }.toMutableMap()
    }

    fun start(counter: PerfCounters) {
        if (timeStamps[counter] != Long.MIN_VALUE) error("Subsequent call to start")
        timeStamps[counter] = System.nanoTime()
    }

    fun stop(counter: PerfCounters) {
        if (timeStamps[counter] == Long.MIN_VALUE) error("Call to Stop without call to Start")
        counters[counter]!!.addValue(System.nanoTime() - timeStamps[counter]!!, timeStamps[counter]!!)
        timeStamps[counter] = Long.MIN_VALUE
    }

    fun getTps(counter: PerfCounters): Float {
        return counters[counter]!!.getCount().toFloat() / (counters[counter]!!.getTimePeriod() * nanoSecond)
    }

    fun getAvgTime(counter: PerfCounters): Float {
        return (counters[counter]!!.getSum() * nanoSecond) / counters[counter]!!.getCount().toFloat()
    }

    fun getMinTime(counter: PerfCounters): Float {
        return counters[counter]!!.getMin() * nanoSecond
    }

    fun getMaxTime(counter: PerfCounters): Float {
        return counters[counter]!!.getMax() * nanoSecond
    }

    fun getAll(): PerformanceCountersResponse {

        return PerformanceCountersResponse(PerfCounters.values().map {
            PerformanceCounterResponse(
                it,
                String.format("%.6f", getTps(it)),
                String.format("%.6f", getMinTime(it)),
                String.format("%.6f", getMaxTime(it)),
                String.format("%.6f", getAvgTime(it)),
            )
        }.toMutableList())
    }
}
