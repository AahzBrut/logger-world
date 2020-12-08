package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.dto.inner.logging.LogValueData
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.mapper.logging.ArrivalEventMapper
import io.github.loggerworld.mapper.logging.DepartureEventMapper
import io.github.loggerworld.mapper.logging.LoggingDataMapper
import io.github.loggerworld.mapper.logging.LoginEventMapper
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.repository.logging.LogClassRepository
import io.github.loggerworld.repository.logging.LogEntryRepository
import io.github.loggerworld.repository.logging.LogEntryValsRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class LoggingDomainService(
    private val logClassRepository: LogClassRepository,
    private val loggingDataMapper: LoggingDataMapper,
    private val logEntryRepository: LogEntryRepository,
    private val logEntryValsRepository: LogEntryValsRepository,
    private val loginEventMapper: LoginEventMapper,
    private val departureEventMapper: DepartureEventMapper,
    private val arrivalEventMapper: ArrivalEventMapper,
) {

    private lateinit var logMessagesTemplates: LoggingData
    private val batch: MutableList<LogEntry> = mutableListOf()

    fun setLogMessagesTemplates(templates: LoggingData) {
        logMessagesTemplates = templates
    }

    @Transactional
    fun getLogMessagesSettings(): LoggingData {

        return loggingDataMapper.fromList(logClassRepository.findAll())
    }

    fun addLoginMessageToBatch(event: LoginEvent, messageId: Int) {

        batch.add(loginEventMapper.from(event, messageId))
    }

    @Transactional
    fun commitBatch() {
        logEntryRepository.saveAll(batch)
        batch.clear()
    }

    fun addArrivalMessageToBatch(event: ArrivalEvent, messageId: Int) {
        batch.add(arrivalEventMapper.from(event, messageId))
    }

    fun addDepartureMessageToBatch(event: DepartureEvent, messageId: Int) {

        batch.add(departureEventMapper.from(event, messageId))
    }

    @Transactional
    fun getPlayerLogEntries(
        activePlayerId: Long,
        language: Languages
    ): List<Pair<PlayerLogEntryResponse, List<LogValueData>>> {

        val logEntries = logEntryRepository.findAllByPlayerId(activePlayerId)
        val entriesVals = logEntryValsRepository.findAllByPlayerId(activePlayerId)

        return logEntries.map { entry ->
            Pair(
                PlayerLogEntryResponse(
                    entry.createdAt!!,
                    entry.logClass,
                    entry.logType,
                    getMessageById(entry.logClass, entry.logType, entry.logTypeMessage.id!!, language)
                ),
                entriesVals
                    .filter {
                        it.logEntry.id == entry.id
                    }
                    .map {
                        LogValueData(
                            it.ordinal,
                            it.type,
                            it.value
                        )
                    }
                    .toList()
            )
        }.toList()
    }

    private fun getMessageById(
        logClass: LogClasses,
        logType: LogTypes,
        messageId: Int,
        language: Languages
    ): String {
        return logMessagesTemplates
            .classes[logClass]!!
            .types[logType]!!
            .templates
            .values
            .first {
                it.messageId == messageId
            }
            .messages[language]!!
    }
}