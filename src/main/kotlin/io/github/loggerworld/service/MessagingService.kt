package io.github.loggerworld.service

import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.messagebus.event.WrongCommandEvent
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_GAMEPLAY_LOCATION_NOTIFICATION_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_LOG_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_WRONG_COMMAND_QUEUE
import io.github.loggerworld.util.logger
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MessagingService(
    private val locationNotificationBus: EventBus<LocationChangedEvent>,
    private val wrongCommandEventBus: EventBus<WrongCommandEvent>,
    private val playerService: PlayerService,
    private val userService: UserService,
    private val simpleMessagingTemplate: SimpMessagingTemplate,
    private val performanceCounter: PerformanceCounter
) : LogAware {

    @Scheduled(fixedDelay = 5, initialDelay = 100)
    fun sendMessages() {

        performanceCounter.start(PerfCounters.MESSAGING_SERVICE)

        while (locationNotificationBus.receiveEvent { event ->
                logger().debug("\nEvent of player is arrived to location is ready to send: $event")
                notifyOnArrival(event)
            }) {
            // Empty body
        }

        while (wrongCommandEventBus.receiveEvent { event ->
                val player = playerService.getPlayerById(event.playerId)
                val user = userService.getUserById(player.userId)

                simpleMessagingTemplate.convertAndSendToUser(user.loginName, WS_GAMEPLAY_WRONG_COMMAND_QUEUE, event)
            }) {
            // Empty body
        }

        performanceCounter.stop(PerfCounters.MESSAGING_SERVICE)
    }

    fun sendMessageToPlayer(loginName: String, event: PlayerLogEntryResponse) {
        simpleMessagingTemplate.convertAndSendToUser(loginName, WS_GAMEPLAY_LOG_QUEUE, event)
    }

    private fun notifyOnArrival(event: LocationChangedEvent) {
        event.players.forEach {
            if (playerService.isPlayerUserOnline(it.id)) {
                val player = playerService.getPlayerById(it.id)

                val user = userService.getUserById(player.userId)

                simpleMessagingTemplate.convertAndSendToUser(
                    user.loginName,
                    WS_GAMEPLAY_LOCATION_NOTIFICATION_QUEUE,
                    event
                )
            }
        }
    }


}