package io.github.loggerworld.util

import java.util.concurrent.atomic.AtomicInteger

class RingBuffer(private val size: Int = 500) {

    private val values: Array<Long> = Array(size) { Long.MIN_VALUE }
    private val timeStamps: Array<Long> = Array(size) { Long.MIN_VALUE }
    private var head: AtomicInteger = AtomicInteger(0)

    fun addValue(value: Long, timeStampNano: Long) {
        timeStamps[head.get()] = timeStampNano
        values[head.getAndIncrement()] = value
        head.compareAndSet(size, 0)
    }

    fun getMinMaxDiff(): Long {
        return getMinMaxDiffArray(values)
    }

    fun getCount(): Long {
        var count = 0L

        for (value in values) {
            if (value == Long.MIN_VALUE) continue
            count++
        }
        return count
    }

    fun getMin(): Long {
        var minValue = Long.MAX_VALUE

        for (value in values) {
            if (value == Long.MIN_VALUE) continue
            if (value < minValue) minValue = value
        }
        return minValue
    }

    fun getMax(): Long {
        var maxValue = Long.MIN_VALUE

        for (value in values) {
            if (value == Long.MIN_VALUE) continue
            if (value > maxValue) maxValue = value
        }
        return maxValue
    }

    fun getSum(): Long {
        var sumValue = 0L

        for (value in values) {
            if (value == Long.MIN_VALUE) continue
            sumValue += value
        }
        return sumValue
    }

    fun getTimePeriod(): Long {
        return getMinMaxDiffArray(timeStamps)
    }

    private fun getMinMaxDiffArray(array: Array<Long>) : Long {
        var maxValue = Long.MIN_VALUE
        var minValue = Long.MAX_VALUE

        for (value in array) {
            if (value == Long.MIN_VALUE) continue
            if (value > maxValue) maxValue = value
            if (value < minValue) minValue = value
        }
        return maxValue - minValue
    }
}
