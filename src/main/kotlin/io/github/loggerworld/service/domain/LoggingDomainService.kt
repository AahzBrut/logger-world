package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.domain.logging.LogEntryVals
import io.github.loggerworld.domain.logging.LogTypeMessage
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.mapper.logging.LoggingDataMapper
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.repository.logging.LogClassRepository
import io.github.loggerworld.repository.logging.LogEntryRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class LoggingDomainService(
    private val logClassRepository: LogClassRepository,
    private val loggingDataMapper: LoggingDataMapper,
    private val logEntryRepository: LogEntryRepository,
) {

    private val batch: MutableList<LogEntry> = mutableListOf()

    @Transactional
    fun getLogMessagesSettings(): LoggingData {

        return loggingDataMapper.fromList(logClassRepository.findAll())
    }

    fun addLoginMessageToBatch(event: LoginEvent, messageId: Int) {
        //TODO("Убрать в мапперы")
        val logEntry = LogEntry(
            logTypeMessage = LogTypeMessage().also { it.id = messageId },
            player = Player().also { it.id = event.playerId },
            logClass = LogTypes.LOGIN.logClass,
            logType = LogTypes.LOGIN,
            event.created
        )

        val logEntryVal = LogEntryVals(
            logEntry,
            Player().also {
                it.id = event.playerId
            }, 1,
            event.locationId.toString()
        )

        logEntry.logEntryVals.add(logEntryVal)

        batch.add(logEntry)
    }

    @Transactional
    fun commitBatch() {
        logEntryRepository.saveAll(batch)
        batch.clear()
    }

    fun addArrivalMessageToBatch(event: ArrivalEvent, messageId: Int) {
        //TODO("Убрать в мапперы")
        val logEntry = LogEntry(
            logTypeMessage = LogTypeMessage().also { it.id = messageId },
            player = Player().also { it.id = event.playerId },
            logClass = LogTypes.ARRIVAL.logClass,
            logType = LogTypes.ARRIVAL,
            event.created
        )

        val logEntryVal1 = LogEntryVals(
            logEntry,
            Player().also {
                it.id = event.playerId
            }, 1,
            event.fromLocationId.toString()
        )

        val logEntryVal2 = LogEntryVals(
            logEntry,
            Player().also {
                it.id = event.playerId
            }, 2,
            event.toLocationId.toString()
        )

        logEntry.logEntryVals.add(logEntryVal1)
        logEntry.logEntryVals.add(logEntryVal2)

        batch.add(logEntry)
    }

    fun addDepartureMessageToBatch(event: DepartureEvent, messageId: Int) {
        //TODO("Убрать в мапперы")
        val logEntry = LogEntry(
            logTypeMessage = LogTypeMessage().also {it.id = messageId},
            player = Player().also {it.id = event.playerId},
            logClass = LogTypes.DEPARTURE.logClass,
            logType = LogTypes.DEPARTURE,
            event.created)

        val logEntryVal1 = LogEntryVals(
            logEntry,
            Player().also {
                it.id = event.playerId
            }, 1,
            event.fromLocationId.toString()
        )

        val logEntryVal2 = LogEntryVals(
            logEntry,
            Player().also {
                it.id = event.playerId
            }, 2,
            event.toLocationId.toString()
        )

        logEntry.logEntryVals.add(logEntryVal1)
        logEntry.logEntryVals.add(logEntryVal2)

        batch.add(logEntry)
    }
}