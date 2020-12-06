package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogClass
import org.springframework.data.jpa.repository.JpaRepository

interface LogClassRepository : JpaRepository<LogClass, Byte>
