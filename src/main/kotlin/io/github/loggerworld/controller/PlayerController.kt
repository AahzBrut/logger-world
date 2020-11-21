package io.github.loggerworld.controller

import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.service.PlayerService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class PlayerController(
    private val playerService: PlayerService
) {

    @MessageMapping("/player")
    @SendTo("/player/messages")
    fun getAllPlayers(principal: Principal) : PlayersResponse {
        return playerService.getAllPlayers(principal.name)
    }

    @MessageMapping("/player/classes")
    @SendTo("/player/classes/messages")
    fun getAllClasses(principal: Principal) : PlayerClassesResponse {
        return playerService.getAllClasses(principal.name)
    }
}