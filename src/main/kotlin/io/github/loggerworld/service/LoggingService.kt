package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.dto.response.logging.PlayerLogsResponse
import io.github.loggerworld.dto.response.user.UserResponse
import io.github.loggerworld.mapper.logging.LogEntryFromArrivalEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromDepartureEventMapper
import io.github.loggerworld.mapper.logging.LogEntryFromLoginEventMapper
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.ArrivalEvent
import io.github.loggerworld.messagebus.event.DepartureEvent
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.service.domain.LoggingDomainService
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
) : LogAware {

    private lateinit var logMessagesTemplates: LoggingData

    private val valueDecoders: Map<LogValueTypes, (String, Languages) -> String> =
        mapOf(
            LogValueTypes.LOCATION_ID to locationService::decodeLocation,
            LogValueTypes.PLAYER_ID to playerService::decodePlayer,
        )

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
            when (event) {
                is LoginEvent -> {
                    val messageId = getRandomMessage(LogTypes.LOGIN)
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
                is ArrivalEvent -> {
                    val messageId = getRandomMessage(LogTypes.ARRIVAL)
                    val user = playerService.getUserByPlayerId(event.playerId)
                    loggingDomainService.addArrivalMessageToBatch(event,messageId)
                    messagingService.sendMessageToPlayer(
                        user.loginName, arrivalEventMapper.from(
                            event,
                            getMessageByIdAndLanguage(messageId, user.language),
                            valueDecoders,
                            user.language
                        )
                    )
                }
                is DepartureEvent -> {
                    val messageId = getRandomMessage(LogTypes.DEPARTURE)
                    val user = playerService.getUserByPlayerId(event.playerId)
                    loggingDomainService.addDepartureMessageToBatch(event,messageId)
                    messagingService.sendMessageToPlayer(
                        user.loginName, departureEventMapper.from(
                            event,
                            getMessageByIdAndLanguage(messageId, user.language),
                            valueDecoders,
                            user.language
                        )
                    )
                }
            }

            logEventBus.destroyEvent(event)
        }
        loggingDomainService.commitBatch()
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