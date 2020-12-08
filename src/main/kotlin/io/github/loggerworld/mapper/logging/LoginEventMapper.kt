package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.domain.logging.LogEntryVals
import io.github.loggerworld.domain.logging.LogTypeMessage
import io.github.loggerworld.mapper.LogEntryMapper
import io.github.loggerworld.messagebus.event.LoginEvent
import org.springframework.stereotype.Service

@Service
class LoginEventMapper : LogEntryMapper<LoginEvent> {

    override fun from(source: LoginEvent, messageId: Int): LogEntry {

        val logEntry = LogEntry(
            logTypeMessage = LogTypeMessage().also { it.id = messageId },
            player = Player().also { it.id = source.playerId },
            logClass = LogTypes.LOGIN.logClass,
            logType = LogTypes.LOGIN,
            source.created
        )

        val logEntryVal = LogEntryVals(
            logEntry,
            Player().also { it.id = source.playerId },
            1,
            LogValueTypes.LOCATION_ID,
            source.locationId.toString()
        )

        logEntry.logEntryVals.add(logEntryVal)

        return logEntry
    }
}