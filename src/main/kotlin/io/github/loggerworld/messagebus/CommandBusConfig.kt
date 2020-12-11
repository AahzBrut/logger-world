package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.PlayerKickMonsterNestCommand
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
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
}