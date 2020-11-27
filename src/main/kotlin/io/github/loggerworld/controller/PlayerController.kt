package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.PlayerMoveRequest
import io.github.loggerworld.dto.request.PlayerStartGameRequest
import io.github.loggerworld.service.PlayerService
import io.github.loggerworld.util.WS_PLAYERS_MOVE
import io.github.loggerworld.util.WS_PLAYERS_START
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class PlayerController(
    private val playerService: PlayerService,
) {

    @MessageMapping(WS_PLAYERS_START)
    fun startGameForPlayer(principal: Principal, request: PlayerStartGameRequest) {
        playerService.startGameForPlayer(principal.name, request)
    }

    @MessageMapping(WS_PLAYERS_MOVE)
    fun movePlayer(principal: Principal, request: PlayerMoveRequest) {
        playerService.movePlayer(principal.name, request)
    }
}