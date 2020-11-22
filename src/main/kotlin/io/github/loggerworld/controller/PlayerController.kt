package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.PlayerStartGameRequest
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.service.PlayerService
import io.github.loggerworld.util.WS_DS_PLAYER_CLASSES_MESSAGES
import io.github.loggerworld.util.WS_DS_PLAYER_MESSAGES
import io.github.loggerworld.util.WS_PLAYERS_CLASSES_GET_ALL
import io.github.loggerworld.util.WS_PLAYERS_GET_ALL
import io.github.loggerworld.util.WS_PLAYERS_NEW
import io.github.loggerworld.util.WS_PLAYERS_START
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class PlayerController(
    private val playerService: PlayerService
) {

    @MessageMapping(WS_PLAYERS_GET_ALL)
    @SendTo(WS_DS_PLAYER_MESSAGES)
    fun getAllPlayers(principal: Principal) : PlayersResponse {
        return playerService.getAllPlayers(principal.name)
    }

    @MessageMapping(WS_PLAYERS_NEW)
    fun addNewPlayer(principal: Principal, request: PlayerAddRequest) {
        playerService.addNewPlayer(principal.name, request)
    }

    @MessageMapping(WS_PLAYERS_CLASSES_GET_ALL)
    @SendTo(WS_DS_PLAYER_CLASSES_MESSAGES)
    fun getAllClasses(principal: Principal) : PlayerClassesResponse {
        return playerService.getAllClasses(principal.name)
    }

    @MessageMapping(WS_PLAYERS_START)
    fun startGameForPlayer(principal: Principal, request: PlayerStartGameRequest) {
        playerService.startGameForPlayer(principal.name, request)
    }
}