package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogEntryVals
import org.springframework.data.jpa.repository.JpaRepository

interface LogEntryValsRepository : JpaRepository<LogEntryVals, Long> {

    fun findAllByPlayerId(playerId: Long): List<LogEntryVals>
}
