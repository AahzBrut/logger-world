package io.github.loggerworld.controller

import io.github.loggerworld.dto.response.ResponseObject
import io.github.loggerworld.dto.response.perfcounter.PerformanceCountersResponse
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


const val PERFORMANCE_COUNTERS_URL = "/api/admin/performance-counters"

@RestController
class AdminRestController(
    private val performanceCounter: PerformanceCounter
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PERFORMANCE_COUNTERS_URL)
    fun getAllPlayers(principal: Principal): ResponseObject<PerformanceCountersResponse> {
        return ResponseObject(success = true, data = performanceCounter.getAll())
    }

}