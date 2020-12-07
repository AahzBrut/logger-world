package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogTypeMessage
import org.springframework.data.jpa.repository.JpaRepository

interface LogTypeMessageRepository : JpaRepository<LogTypeMessage, Int>
