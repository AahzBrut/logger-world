package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.PlayerKickMonsterNestCommand
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import io.github.loggerworld.messagebus.event.SerializeItemsDropFromMobCommand
import org.springframework.beans.factory.annotation.Qualifier
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
    @Qualifier("Serializer")
    fun serializeItemsDropFromMobCommandBus() : EventBus<SerializeItemsDropFromMobCommand> =
        EventBus(SerializeItemsDropFromMobCommand::class)

    @Bean
    @Qualifier("Deserializer")
    fun deserializeItemsDropFromMobCommandBus() : EventBus<SerializeItemsDropFromMobCommand> =
        EventBus(SerializeItemsDropFromMobCommand::class)
}