package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.domain.logging.LogEntryVals
import io.github.loggerworld.domain.logging.LogTypeMessage
import io.github.loggerworld.mapper.LogEntryMapper
import io.github.loggerworld.messagebus.event.ArrivalEvent
import org.springframework.stereotype.Service

@Service
class ArrivalEventMapper : LogEntryMapper<ArrivalEvent> {

    override fun from(source: ArrivalEvent, messageId: Int): LogEntry {

        val logEntry = LogEntry(
            logTypeMessage = LogTypeMessage().also { it.id = messageId },
            player = Player().also { it.id = source.playerId },
            logClass = LogTypes.ARRIVAL.logClass,
            logType = LogTypes.ARRIVAL,
            source.created
        )

        val logEntryVal1 = LogEntryVals(
            logEntry,
            Player().also { it.id = source.playerId },
            1,
            LogValueTypes.LOCATION_ID,
            source.toLocationId.toString()
        )

        val logEntryVal2 = LogEntryVals(
            logEntry,
            Player().also { it.id = source.playerId },
            2,
            LogValueTypes.LOCATION_ID,
            source.toLocationId.toString()
        )

        logEntry.logEntryVals.add(logEntryVal1)
        logEntry.logEntryVals.add(logEntryVal2)

        return logEntry
    }
}