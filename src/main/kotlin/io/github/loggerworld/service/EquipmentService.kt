package io.github.loggerworld.service

import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.EquipmentChangedEvent
import io.github.loggerworld.service.domain.EquipmentDomainService
import io.github.loggerworld.service.perfcount.PerfCounters
import io.github.loggerworld.service.perfcount.PerformanceCounter
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE
import io.github.loggerworld.util.logger
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class EquipmentService(
    private val equipmentDomainService: EquipmentDomainService,
    private val equipmentChangedEventBus: EventBus<EquipmentChangedEvent>,
    private val performanceCounter: PerformanceCounter,
    private val playerService: PlayerService,
    private val userService: UserService,
    private val simpleMessagingTemplate: SimpMessagingTemplate,
) : LogAware {

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache() {
        equipmentDomainService.initEquipmentSlotTypeDescriptions()
        logger().debug("Equipment type descriptions loaded into cache.")
    }

    @Suppress("kotlin:S1144")
    @Scheduled(fixedDelay = 10, initialDelay = 100)
    private fun notifyEquipmentChange() {
        performanceCounter.start(PerfCounters.EQUIPMENT_CHANGE_NOTIFICATION_SERVICE)

        while (equipmentChangedEventBus.receiveEvent {
                val player = playerService.getPlayerById(it.playerId)
                val user = userService.getUserById(player.userId)

                simpleMessagingTemplate.convertAndSendToUser(user.loginName, WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE, it)
            }) {
            // Empty body
        }

        performanceCounter.stop(PerfCounters.EQUIPMENT_CHANGE_NOTIFICATION_SERVICE)
    }
}