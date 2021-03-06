package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.CombatEvent
import io.github.loggerworld.messagebus.event.EquipmentChangedEvent
import io.github.loggerworld.messagebus.event.InventoryChangedEvent
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.messagebus.event.SerializeItemsDropFromMobCommand
import io.github.loggerworld.messagebus.event.WrongCommandEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NotificationBusConfig {

    @Bean
    fun locationChangedNotificationBus() : EventBus<LocationChangedEvent> =
        EventBus(LocationChangedEvent::class)

    @Bean
    fun wrongCommandNotificationBus() : EventBus<WrongCommandEvent> =
        EventBus(WrongCommandEvent::class)

    @Bean
    fun serializeItemsDropFromMobCommandBus() : EventBus<SerializeItemsDropFromMobCommand> =
        EventBus(SerializeItemsDropFromMobCommand::class)

    @Bean
    fun inventoryChangeNotificationBus() : EventBus<InventoryChangedEvent> =
        EventBus(InventoryChangedEvent::class)

    @Bean
    fun equipmentChangeNotificationBus() : EventBus<EquipmentChangedEvent> =
        EventBus(EquipmentChangedEvent::class)

    @Bean
    fun combatNotificationBus() : EventBus<CombatEvent> =
        EventBus(CombatEvent::class)
}