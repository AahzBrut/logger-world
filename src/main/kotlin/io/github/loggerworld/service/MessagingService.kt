package io.github.loggerworld.service

import io.github.loggerworld.messagebus.NotificationEventBus
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.messagebus.event.WrongCommandEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_GAMEPLAY_LOCATION_NOTIFICATIO_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_WRONG_COMMAND_QUEUE
import io.github.loggerworld.util.logger
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MessagingService(
    private val locationNotificationBus: NotificationEventBus<LocationChangedEvent>,
    private val wrongCommandEventBus: NotificationEventBus<WrongCommandEvent>,
    private val playerService: PlayerService,
    private val userService: UserService,
    private val simpleMessagingTemplate: SimpMessagingTemplate,
) : LogAware {

    @Scheduled(fixedDelay = 5, initialDelay = 100)
    fun sendMessages() {

        while (!locationNotificationBus.isQueueEmpty()) {

            val event = locationNotificationBus.popEvent()

            logger().debug("\nEvent of player is arrived to location is ready to send: $event")

            notifyOnArrival(event)

            locationNotificationBus.destroyEvent(event)
        }

        while (!wrongCommandEventBus.isQueueEmpty()){
            val event = wrongCommandEventBus.popEvent()
            val player = playerService.getPlayerById(event.playerId)
            val user = userService.getUserById(player.userId)

            simpleMessagingTemplate.convertAndSendToUser(user.loginName, WS_GAMEPLAY_WRONG_COMMAND_QUEUE, event)

            wrongCommandEventBus.destroyEvent(event)
        }
    }

    private fun notifyOnArrival(event: LocationChangedEvent) {
        event.players.forEach {
            val player = playerService.getPlayerById(it.id)

            val user = userService.getUserById(player.userId)

            simpleMessagingTemplate.convertAndSendToUser(user.loginName, WS_GAMEPLAY_LOCATION_NOTIFICATIO_QUEUE, event)
        }
    }


}