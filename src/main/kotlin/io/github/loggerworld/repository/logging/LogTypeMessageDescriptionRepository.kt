package io.github.loggerworld.repository.logging

import io.github.loggerworld.domain.logging.LogTypeMessageDescription
import org.springframework.data.jpa.repository.JpaRepository

interface LogTypeMessageDescriptionRepository :JpaRepository<LogTypeMessageDescription, Int>
