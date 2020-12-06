package io.github.loggerworld.service

import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.service.domain.LoggingDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class LoggingService(
    private val loggingDomainService: LoggingDomainService,
    private val logEventBus: LogEventBus<LogEvent>
) : LogAware {

    private lateinit var logMessagesTemplates: LoggingData

    @PostConstruct
    fun initLoggingData() {
        logMessagesTemplates = loggingDomainService.getLogMessagesSettings()
        logger().debug("Logging templates loaded.")
    }

    @Scheduled(fixedDelay = 500, initialDelay = 100)
    fun logEvents() {
        while (!logEventBus.isQueueEmpty()) {

            val event = logEventBus.popEvent()

            logger().debug("\n\nEvent for logging(class:${event.javaClass}): $event")

            logEventBus.destroyEvent(event)
        }
    }
}