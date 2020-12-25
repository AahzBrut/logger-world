package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.domain.logging.LogEntryVals
import io.github.loggerworld.domain.logging.LogTypeMessage
import io.github.loggerworld.mapper.LogEntryMapper
import io.github.loggerworld.messagebus.event.PlayerGotItemEvent
import org.springframework.stereotype.Service


@Service
class PlayerGotItemEventMapper : LogEntryMapper<PlayerGotItemEvent> {

    override fun from(source: PlayerGotItemEvent, messageId: Int): LogEntry {
        val logEntry = LogEntry(
            logTypeMessage = LogTypeMessage().also { it.id = messageId },
            player = Player().also { it.id = source.playerId },
            logClass = source.eventType.logClass,
            logType = source.eventType,
            source.created
        )

        val logEntryVal = LogEntryVals(
            logEntry,
            Player().also { it.id = source.playerId },
            1,
            LogValueTypes.ITEM_ID,
            "${source.itemData.category.ordinal},${source.itemData.quality.ordinal},${source.itemData.quantity}"
        )

        logEntry.logEntryVals.add(logEntryVal)

        return logEntry
    }
}