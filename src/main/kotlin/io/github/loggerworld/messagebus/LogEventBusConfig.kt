package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.LogEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class LogEventBusConfig {

    @Bean
    fun getLogEventBus() : LogEventBus<LogEvent>{

        return LogEventBus()
    }
}