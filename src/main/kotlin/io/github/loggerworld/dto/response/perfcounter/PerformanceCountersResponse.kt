package io.github.loggerworld.dto.response.perfcounter

data class PerformanceCountersResponse(
    var counters: MutableList<PerformanceCounterResponse> = mutableListOf()
)
