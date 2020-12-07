package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogTypeDescription
import org.springframework.data.jpa.repository.JpaRepository

interface LogTypeDescriptionRepository : JpaRepository<LogTypeDescription, Short>
