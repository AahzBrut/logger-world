package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.response.ResponseObject
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayerStatsResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.dto.response.logging.PlayerLogsResponse
import io.github.loggerworld.service.LoggingService
import io.github.loggerworld.service.PlayerService
import io.github.loggerworld.service.UserService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

const val PLAYERS_URL = "/api/players"
const val PLAYERS_CLASSES_URL = "/api/players/classes"
const val PLAYERS_STATS_URL = "/api/players/stats"
const val PLAYERS_LOGS_URL = "/api/players/logs"

@RestController
class PlayerRestController(
    private val playerService: PlayerService,
    private val userService: UserService,
    private val loggingService: LoggingService,
) : LogAware {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PLAYERS_URL)
    fun getAllPlayers(principal: Principal): ResponseObject<PlayersResponse> {
        return ResponseObject(success = true, data = playerService.getAllPlayers(principal.name))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PLAYERS_CLASSES_URL)
    fun getAllClasses(principal: Principal): ResponseObject<PlayerClassesResponse> {
        return ResponseObject(success = true, data = playerService.getAllClasses(principal.name))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PLAYERS_STATS_URL)
    fun getAllStats(principal: Principal): ResponseObject<PlayerStatsResponse> {
        return ResponseObject(
            success = true,
            data = playerService.getPlayerStats(userService.getUserLanguage(principal.name))
        )
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(PLAYERS_URL)
    fun addNewPlayer(principal: Principal, @RequestBody request: PlayerAddRequest): ResponseObject<PlayerResponse> {
        return ResponseObject(success = true, data = playerService.addNewPlayer(principal.name, request))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PLAYERS_LOGS_URL)
    fun getLogs(principal: Principal): ResponseObject<PlayerLogsResponse> {

        val responseObject = ResponseObject(
            success = true,
            data = loggingService.getPlayerLogs(userService.getUserByName(principal.name))
        )
        logger().debug("\n\nResponseObject:\n$responseObject")

        return responseObject
    }
}