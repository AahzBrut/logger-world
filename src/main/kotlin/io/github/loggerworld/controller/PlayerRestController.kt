package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.service.PlayerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

const val PLAYERS_URL = "/api/players"
const val PLAYERS_CLASSES_URL = "/api/players/classes"

@RestController
class PlayerRestController(
    private val playerService: PlayerService
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PLAYERS_URL)
    fun getAllPlayers(principal: Principal) : PlayersResponse {
        return playerService.getAllPlayers(principal.name)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PLAYERS_CLASSES_URL)
    fun getAllClasses(principal: Principal) : PlayerClassesResponse {
        return playerService.getAllClasses(principal.name)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(PLAYERS_URL)
    fun addNewPlayer(principal: Principal, @RequestBody request: PlayerAddRequest) : PlayerResponse {
        return playerService.addNewPlayer(principal.name, request)
    }
}