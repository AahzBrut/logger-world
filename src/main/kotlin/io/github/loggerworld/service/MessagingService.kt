package io.github.loggerworld.service

import io.github.loggerworld.messagebus.OutGoingEventBus
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_GAMEPLAY_EVENTS_QUEUE
import io.github.loggerworld.util.logger
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MessagingService(
    private val outGoingEventBus: OutGoingEventBus,
    private val playerService: PlayerService,
    private val userService: UserService,
    private val simpleMessagingTemplate: SimpMessagingTemplate,
) : LogAware {

    @Scheduled(fixedDelay = 5, initialDelay = 500)
    fun sendMessages() {

        while (!outGoingEventBus.isQueueEmpty()) {

            val event = outGoingEventBus.popEvent()

            logger().info("Event of player is arrived to location is ready to send: $event")
            val player = playerService.getPlayerById(event.playerId)

            val user = userService.getUserById(player.userId)

            simpleMessagingTemplate.convertAndSendToUser(user.loginName, WS_GAMEPLAY_EVENTS_QUEUE, event)
        }
    }
}