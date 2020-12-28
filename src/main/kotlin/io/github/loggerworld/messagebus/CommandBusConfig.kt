package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.DeserializeItemsDropFromMobCommand
import io.github.loggerworld.messagebus.event.PlayerEquipItemCommand
import io.github.loggerworld.messagebus.event.PlayerKickMonsterNestCommand
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.PlayerRequestEquipmentCommand
import io.github.loggerworld.messagebus.event.PlayerRequestInventoryCommand
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandBusConfig {

    @Bean
    fun moveCommandBus() : EventBus<PlayerMoveCommand> =
        EventBus(PlayerMoveCommand::class)

    @Bean
    fun startGameCommandBus() : EventBus<PlayerStartCommand> =
        EventBus(PlayerStartCommand::class)

    @Bean
    fun playerKickMonsterNestCommandBus() : EventBus<PlayerKickMonsterNestCommand> =
        EventBus(PlayerKickMonsterNestCommand::class)

    @Bean
    fun deserializeItemsDropFromMobCommandBus() : EventBus<DeserializeItemsDropFromMobCommand> =
        EventBus(DeserializeItemsDropFromMobCommand::class)

    @Bean
    fun equipItemCommandBus() : EventBus<PlayerEquipItemCommand> =
        EventBus(PlayerEquipItemCommand::class)

    @Bean
    fun requestInventoryCommandBus() : EventBus<PlayerRequestInventoryCommand> =
        EventBus(PlayerRequestInventoryCommand::class)

    @Bean
    fun requestEquipmentCommandBus() : EventBus<PlayerRequestEquipmentCommand> =
        EventBus(PlayerRequestEquipmentCommand::class)
}