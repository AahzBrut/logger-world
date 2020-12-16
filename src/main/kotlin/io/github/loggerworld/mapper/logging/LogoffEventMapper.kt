package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.domain.logging.LogTypeMessage
import io.github.loggerworld.mapper.LogEntryMapper
import io.github.loggerworld.messagebus.event.LogoffEvent
import org.springframework.stereotype.Service

@Service
class LogoffEventMapper : LogEntryMapper<LogoffEvent> {

    override fun from(source: LogoffEvent, messageId: Int): LogEntry {

        return LogEntry(
            logTypeMessage = LogTypeMessage().also { it.id = messageId },
            player = Player().also { it.id = source.playerId },
            logClass = source.eventType.logClass,
            logType = source.eventType,
            source.created
        )
    }
}