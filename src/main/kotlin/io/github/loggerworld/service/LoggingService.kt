package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.dto.response.logging.PlayerLogsResponse
import io.github.loggerworld.dto.response.user.UserResponse
import io.github.loggerworld.mapper.logging.LogEntryFromArrivalEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromAttackMobEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromAttackedByMobEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromDealDamageToMobEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromDepartureEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromLoginEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromNestKickedEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromPlayerGotItemEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromPlayerKillMobEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromPlayerKilledByMobEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromReceiveDamageFromMobEventMapper
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.AttackMobEvent
import io.github.loggerworld.messagebus.event.AttackedByMobEvent
import io.github.loggerworld.messagebus.event.DealDamageToMobEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.messagebus.event.LogoffEvent
import io.github.loggerworld.messagebus.event.NestKickEvent
import io.github.loggerworld.messagebus.event.PlayerGotItemEvent
import io.github.loggerworld.messagebus.event.PlayerKillMobEvent
import io.github.loggerworld.messagebus.event.PlayerKilledByMobEvent
import io.github.loggerworld.messagebus.event.ReceiveDamageFromMobEvent
import io.github.loggerworld.service.domain.LoggingDomainService
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.random.Random


@Service
class LoggingService(
    private val loggingDomainService: LoggingDomainService,
    private val logEventBus: LogEventBus<LogEvent>,
    private val playerService: PlayerService,
    private val locationService: LocationService,
    private val messagingService: MessagingService,
    private val loginEventMapper: LogEntryFromLoginEventMapper,
    private val arrivalEventMapper: LogEntryFromArrivalEventMapper,
    private val departureEventMapper: LogEntryFromDepartureEventMapper,
    private val nestKickedEventMapper: LogEntryFromNestKickedEventMapper,
    private val attackedByMobEventMapper: LogEntryFromAttackedByMobEventMapper,
    private val attackMobEventMapper: LogEntryFromAttackMobEventMapper,
    private val dealDamageToMobMapper: LogEntryFromDealDamageToMobEventMapper,
    private val receiveDamageFromMobEventMapper: LogEntryFromReceiveDamageFromMobEventMapper,
    private val playerKilledByMobMapper: LogEntryFromPlayerKilledByMobEventMapper,
    private val playerKillMobMapper: LogEntryFromPlayerKillMobEventMapper,
    private val playerGotItemMapper: LogEntryFromPlayerGotItemEventMapper,
    private val monsterService: MonsterService,
    private val performanceCounter: PerformanceCounter,
    private val itemService: ItemService,
) : LogAware {

    private lateinit var logMessagesTemplates: LoggingData

    private val valueDecoders: Map<LogValueTypes, (String, Languages) -> String> =
        mapOf(
            LogValueTypes.LOCATION_ID to locationService::decodeLocation,
            LogValueTypes.PLAYER_ID to playerService::decodePlayer,
            LogValueTypes.MONSTER_NEST_ID to monsterService::decodeMonsterNest,
            LogValueTypes.MONSTER_ID to monsterService::decodeMonster,
            LogValueTypes.DAMAGE_AMOUNT to ::shortCircuit,
            LogValueTypes.ITEM_ID to itemService::decodeItem,
        )

    @Suppress("UNUSED_PARAMETER")
    private fun shortCircuit(value: String, language: Languages): String {
        return value
    }

    @PostConstruct
    fun initLoggingData() {
        logMessagesTemplates = loggingDomainService.getLogMessagesSettings()
        loggingDomainService.setLogMessagesTemplates(logMessagesTemplates)
        logger().debug("Logging templates loaded.")
    }

    @Scheduled(fixedDelay = 10, initialDelay = 100)
    fun logEvents() {
        performanceCounter.start(PerfCounters.LOGGING_SERVICE)

        while (!logEventBus.isQueueEmpty()) {

            val event = logEventBus.popEvent()
            routeEvent(event)
            logEventBus.destroyEvent(event)
        }
        loggingDomainService.commitBatch()

        performanceCounter.stop(PerfCounters.LOGGING_SERVICE)
    }

    private fun routeEvent(event: LogEvent) {
        when (event) {
            is LoginEvent -> processLoginEvent(event)
            is ArrivalEvent -> processArrivalEvent(event)
            is DepartureEvent -> processDepartureEvent(event)
            is NestKickEvent -> processNestKickEvent(event)
            is AttackedByMobEvent -> processAttackedByMobEvent(event)
            is AttackMobEvent -> processAttackMobEvent(event)
            is DealDamageToMobEvent -> processDealDamageToMobEvent(event)
            is ReceiveDamageFromMobEvent -> processReceiveDamageFromMobEvent(event)
            is PlayerKilledByMobEvent -> processPlayerKilledByMobEvent(event)
            is PlayerKillMobEvent -> processPlayerKillMobEvent(event)
            is LogoffEvent -> processLogoffEvent(event)
            is PlayerGotItemEvent -> processPlayerGotItemEvent(event)
        }
    }

    private fun processPlayerGotItemEvent(event: PlayerGotItemEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addPlayerGotItemEventToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, playerGotItemMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processLogoffEvent(event: LogoffEvent) {
        val messageId = getRandomMessage(event.eventType)
        loggingDomainService.addLogoffEventToBatch(event, messageId)
    }

    private fun processPlayerKilledByMobEvent(event: PlayerKilledByMobEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addPlayerKilledByMobEvent(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, playerKilledByMobMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processPlayerKillMobEvent(event: PlayerKillMobEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addPlayerKillMobMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, playerKillMobMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processDealDamageToMobEvent(event: DealDamageToMobEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addDealDamageToMobMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, dealDamageToMobMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processReceiveDamageFromMobEvent(event: ReceiveDamageFromMobEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addReceiveDamageFromMobMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, receiveDamageFromMobEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processAttackMobEvent(event: AttackMobEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addAttackMobMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, attackMobEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processAttackedByMobEvent(event: AttackedByMobEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addAttackedByMobMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, attackedByMobEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processNestKickEvent(event: NestKickEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addNestKickMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, nestKickedEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processDepartureEvent(event: DepartureEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addDepartureMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, departureEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processArrivalEvent(event: ArrivalEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addArrivalMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, arrivalEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun processLoginEvent(event: LoginEvent) {
        val messageId = getRandomMessage(event.eventType)
        val user = playerService.getUserByPlayerId(event.playerId)
        loggingDomainService.addLoginMessageToBatch(event, messageId)
        messagingService.sendMessageToPlayer(
            user.loginName, loginEventMapper.from(
                event,
                getMessageByIdAndLanguage(messageId, user.language),
                valueDecoders,
                user.language
            )
        )
    }

    private fun getMessageByIdAndLanguage(messageId: Int, language: Languages): String {

        return logMessagesTemplates.classes
            .flatMap {
                it.value.types.values
            }
            .flatMap {
                it.templates.values
            }
            .first {
                it.messageId == messageId
            }.messages[language]!!

    }

    private fun getRandomMessage(type: LogTypes): Int {

        return logMessagesTemplates.classes
            .flatMap {
                it.value.types.entries
            }
            .filter {
                it.key == type
            }
            .map {
                val numVariants = it.value.templates.keys.size
                val variant = Random.nextInt(numVariants).toByte().inc()
                it.value.templates[variant]!!.messageId
            }.first()
    }

    fun getPlayerLogs(userResponse: UserResponse): PlayerLogsResponse {

        val activePlayerId = playerService.getActivePlayer(userResponse.id)

        val playerLogEntries = loggingDomainService.getPlayerLogEntries(
            activePlayerId,
            userResponse.language
        )


        playerLogEntries.forEach {
            it.first.message = it.first.message.format(
                valueDecoders[LogValueTypes.PLAYER_ID]!!.invoke(activePlayerId.toString(), Languages.EN),
                *it.second.map { data ->
                    valueDecoders[data.valueType]!!.invoke(data.value, userResponse.language)
                }.toTypedArray()
            )
        }

        return PlayerLogsResponse(
            playerLogEntries.map {
                it.first
            }
                .toMutableList()
        )
    }
}