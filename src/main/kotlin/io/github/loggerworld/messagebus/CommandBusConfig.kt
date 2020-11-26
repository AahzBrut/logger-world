package io.github.loggerworld.messagebus

import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandBusConfig {

    @Bean
    fun moveCommandBus() : CommandEventBus<PlayerMoveCommand> =
        CommandEventBus(PlayerMoveCommand::class)

    @Bean
    fun startGameCommandBus() : CommandEventBus<PlayerStartCommand> =
        CommandEventBus(PlayerStartCommand::class)
}