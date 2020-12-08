package io.github.loggerworld.mapper

import io.github.loggerworld.domain.logging.LogEntry

interface LogEntryMapper <S> {

    fun from(source: S, messageId: Int) : LogEntry
}