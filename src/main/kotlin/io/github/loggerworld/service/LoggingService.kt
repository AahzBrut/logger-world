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
import io.github.loggerworld.mapper.logging.LogEntryFromReceiveDamageFromMobEventMapper
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.AttackMobEvent
import io.github.loggerworld.messagebus.event.AttackedByMobEvent
import io.github.loggerworld.messagebus.event.DealDamageToMobEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.messagebus.event.NestKickEvent
import io.github.loggerworld.messagebus.event.ReceiveDamageFromMobEvent
import io.github.loggerworld.service.domain.LoggingDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.random.Random
import kotlin.reflect.KClass


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
    private val monsterService: MonsterService,
) : LogAware {

    private lateinit var logMessagesTemplates: LoggingData

    private val valueDecoders: Map<LogValueTypes, (String, Languages) -> String> =
        mapOf(
            LogValueTypes.LOCATION_ID to locationService::decodeLocation,
            LogValueTypes.PLAYER_ID to playerService::decodePlayer,
            LogValueTypes.MONSTER_NEST_ID to monsterService::decodeMonsterNest,
            LogValueTypes.MONSTER_ID to monsterService::decodeMonster,
            LogValueTypes.DAMAGE_AMOUNT to ::shortCircuit,
        )

    private val eventProcessors: Map<KClass<*>, (LogEvent) -> Unit> =
        mapOf(
            LoginEvent::class to ::processLoginEvent,
            ArrivalEvent::class to ::processArrivalEvent,
            DepartureEvent::class to ::processDepartureEvent,
            NestKickEvent::class to ::processNestKickEvent,
            AttackedByMobEvent::class to ::processAttackedByMobEvent,
            AttackMobEvent::class to ::processAttackMobEvent,
            DealDamageToMobEvent::class to ::processDealDamageToMobEvent,
            ReceiveDamageFromMobEvent::class to ::processReceiveDamageFromMobEvent,
        )

    private fun shortCircuit(value: String, language: Languages) : String {
        return  value
    }

    @PostConstruct
    fun initLoggingData() {
        logMessagesTemplates = loggingDomainService.getLogMessagesSettings()
        loggingDomainService.setLogMessagesTemplates(logMessagesTemplates)
        logger().debug("Logging templates loaded.")
    }

    @Scheduled(fixedDelay = 10, initialDelay = 100)
    fun logEvents() {
        while (!logEventBus.isQueueEmpty()) {

            val event = logEventBus.popEvent()
            eventProcessors[event::class]?.invoke(event) ?: error("There is no processor for event ${event::class}")

            logEventBus.destroyEvent(event)
        }
        loggingDomainService.commitBatch()
    }

    private fun processDealDamageToMobEvent(event: LogEvent) {
        if (event !is DealDamageToMobEvent) return
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

    private fun processReceiveDamageFromMobEvent(event: LogEvent) {
        if (event !is ReceiveDamageFromMobEvent) return
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

    private fun processAttackMobEvent(event: LogEvent) {
        if (event !is AttackMobEvent) return
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

    private fun processAttackedByMobEvent(event: LogEvent) {
        if (event !is AttackedByMobEvent) return
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

    private fun processNestKickEvent(event: LogEvent) {
        if (event !is NestKickEvent) return
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

    private fun processDepartureEvent(event: LogEvent) {
        if (event !is DepartureEvent) return
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

    private fun processArrivalEvent(event: LogEvent) {
        if (event !is ArrivalEvent) return
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

    private fun processLoginEvent(event: LogEvent) {
        if (event !is LoginEvent) return
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
            .first{
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