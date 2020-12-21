package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.logging.LogEntry
import io.github.loggerworld.dto.inner.logging.LogValueData
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.mapper.logging.ArrivalEventMapper
import io.github.loggerworld.mapper.logging.AttackMobEventMapper
import io.github.loggerworld.mapper.logging.AttackedByMobEventMapper
import io.github.loggerworld.mapper.logging.DealDamageToMobEventMapper
import io.github.loggerworld.mapper.logging.DepartureEventMapper
import io.github.loggerworld.mapper.logging.LoggingDataMapper
import io.github.loggerworld.mapper.logging.LoginEventMapper
import io.github.loggerworld.mapper.logging.LogoffEventMapper
import io.github.loggerworld.mapper.logging.NestKickedEventMapper
import io.github.loggerworld.mapper.logging.PlayerKillMobEventMapper
import io.github.loggerworld.mapper.logging.PlayerKilledByMobEventMapper
import io.github.loggerworld.mapper.logging.ReceiveDamageFromMobEventMapper
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.AttackMobEvent
import io.github.loggerworld.messagebus.event.AttackedByMobEvent
import io.github.loggerworld.messagebus.event.DealDamageToMobEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.messagebus.event.LogoffEvent
import io.github.loggerworld.messagebus.event.NestKickEvent
import io.github.loggerworld.messagebus.event.PlayerKillMobEvent
import io.github.loggerworld.messagebus.event.PlayerKilledByMobEvent
import io.github.loggerworld.messagebus.event.ReceiveDamageFromMobEvent
import io.github.loggerworld.repository.logging.LogClassRepository
import io.github.loggerworld.repository.logging.LogEntryRepository
import io.github.loggerworld.repository.logging.LogEntryValsRepository
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
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
    private val nestKickedEventMapper: NestKickedEventMapper,
    private val attackedByMobEventMapper: AttackedByMobEventMapper,
    private val attackMobEventMapper: AttackMobEventMapper,
    private val dealDamageToMobEventMapper: DealDamageToMobEventMapper,
    private val receiveDamageFromMobEventMapper: ReceiveDamageFromMobEventMapper,
    private val playerKilledByMobEventMapper: PlayerKilledByMobEventMapper,
    private val playerKillMobEventMapper: PlayerKillMobEventMapper,
    private val logoffEventMapper: LogoffEventMapper,
) : LogAware {

    private lateinit var logMessagesTemplates: LoggingData
    private val batch: MutableList<LogEntry> = mutableListOf()

    fun setLogMessagesTemplates(templates: LoggingData) {
        logMessagesTemplates = templates
    }

    @Transactional
    fun getLogMessagesSettings(): LoggingData {

        return loggingDataMapper.fromList(logClassRepository.findAll())
    }

    @Transactional
    fun commitBatch() {
        if (batch.isEmpty()) return
        logEntryRepository.saveAll(batch)
        batch.clear()
    }

    fun addLoginMessageToBatch(event: LoginEvent, messageId: Int) {
        batch.add(loginEventMapper.from(event, messageId))
    }

    fun addLogoffEventToBatch(event: LogoffEvent, messageId: Int) {
        batch.add(logoffEventMapper.from(event, messageId))
    }

    fun addArrivalMessageToBatch(event: ArrivalEvent, messageId: Int) {
        batch.add(arrivalEventMapper.from(event, messageId))
    }

    fun addDepartureMessageToBatch(event: DepartureEvent, messageId: Int) {
        batch.add(departureEventMapper.from(event, messageId))
    }

    fun addNestKickMessageToBatch(event: NestKickEvent, messageId: Int) {
        batch.add(nestKickedEventMapper.from(event, messageId))
    }

    fun addAttackedByMobMessageToBatch(event: AttackedByMobEvent, messageId: Int) {
        batch.add(attackedByMobEventMapper.from(event, messageId))
    }

    fun addAttackMobMessageToBatch(event: AttackMobEvent, messageId: Int) {
        batch.add(attackMobEventMapper.from(event, messageId))
    }

    fun addDealDamageToMobMessageToBatch(event: DealDamageToMobEvent, messageId: Int) {
        batch.add(dealDamageToMobEventMapper.from(event, messageId))
    }

    fun addReceiveDamageFromMobMessageToBatch(event: ReceiveDamageFromMobEvent, messageId: Int) {
        batch.add(receiveDamageFromMobEventMapper.from(event, messageId))
    }

    fun addPlayerKilledByMobEvent(event: PlayerKilledByMobEvent, messageId: Int) {
        batch.add(playerKilledByMobEventMapper.from(event, messageId))
    }

    fun addPlayerKillMobMessageToBatch(event: PlayerKillMobEvent, messageId: Int) {
        batch.add(playerKillMobEventMapper.from(event, messageId))
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

        if (logMessagesTemplates
                .classes[logClass]!!
                .types[logType]!!
                .templates
                .values
                .none {
                    it.messageId == messageId
                }) {
            logger().debug("$messageId not found.")
        }

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
