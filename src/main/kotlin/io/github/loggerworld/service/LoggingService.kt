package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.dto.response.logging.PlayerLogsResponse
import io.github.loggerworld.dto.response.user.UserResponse
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

    @Scheduled(fixedDelay = 500, initialDelay = 100)
    fun logEvents() {
        while (!logEventBus.isQueueEmpty()) {

            val event = logEventBus.popEvent()

            when (event) {
                is LoginEvent -> loggingDomainService.addLoginMessageToBatch(
                    event,
                    getRandomMessage(LogTypes.LOGIN)
                )
                is ArrivalEvent -> loggingDomainService.addArrivalMessageToBatch(
                    event,
                    getRandomMessage(LogTypes.ARRIVAL)
                )
                is DepartureEvent -> loggingDomainService.addDepartureMessageToBatch(
                    event,
                    getRandomMessage(LogTypes.DEPARTURE)
                )
            }

            logEventBus.destroyEvent(event)
        }
        loggingDomainService.commitBatch()
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