package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.LocationChangedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NotificationBusConfig {

    @Bean
    fun locationChangedNotificationBus() : NotificationEventBus<LocationChangedEvent> =
        NotificationEventBus(LocationChangedEvent::class)
}