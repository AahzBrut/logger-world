package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogType
import org.springframework.data.jpa.repository.JpaRepository

interface LogTypeRepository : JpaRepository<LogType, Byte>
