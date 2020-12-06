package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogClassDescription
import org.springframework.data.jpa.repository.JpaRepository

interface LogClassDescriptionRepository : JpaRepository<LogClassDescription, Short>