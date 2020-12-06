package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogEntry
import org.springframework.data.jpa.repository.JpaRepository

interface LogEntryRepository: JpaRepository<LogEntry, Long>
