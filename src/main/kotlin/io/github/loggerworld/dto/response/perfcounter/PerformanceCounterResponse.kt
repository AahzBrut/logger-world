package io.github.loggerworld.dto.response.perfcounter

import io.github.loggerworld.service.perfcount.PerfCounters

data class PerformanceCounterResponse(
    var name: PerfCounters,
    var tps: String,
    var minTime: String,
    var maxTime: String,
    var avgTime: String,
)
