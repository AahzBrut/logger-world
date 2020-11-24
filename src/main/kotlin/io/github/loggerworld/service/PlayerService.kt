package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.request.PlayerStartGameRequest
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.messagebus.IncomingEventBus
import io.github.loggerworld.service.domain.PlayerDomainService
import io.github.loggerworld.service.domain.UserDomainService
import org.springframework.stereotype.Service

typealias PlayerClassDescriptionsMap = Map<PlayerClasses, Map<Languages, Pair<String, String>>>
typealias PlayerStatDescriptionsMap = Map<PlayerStatEnum, Map<Languages, Pair<String, String>>>


@Service
class PlayerService(
    private val playerDomainService: PlayerDomainService,
    private val userDomainService: UserDomainService,
    private val incomingEventBus: IncomingEventBus,
) {

    fun getAllPlayers(userName: String): PlayersResponse {
        val user = userDomainService.getUserByName(userName)!!
        return PlayersResponse(playerDomainService.getAllPlayers(user.id, user.language))
    }

    fun getAllClasses(userName: String): PlayerClassesResponse {
        val user = userDomainService.getUserByName(userName)!!
        return PlayerClassesResponse(playerDomainService.getAllPlayerClasses(user.id, user.language))
    }

    fun addNewPlayer(userName: String, request: PlayerAddRequest) {
        val user = userDomainService.getUserByName(userName)!!

        playerDomainService.addNewPlayer(user.id, request)
    }

    fun getAllPlayerClassDescriptions(): PlayerClassDescriptionsMap {

        return playerDomainService.getAllPlayerClassDescriptions()
    }

    fun getAllPlayerStatDescriptions() : PlayerStatDescriptionsMap {

        return playerDomainService.getAllPlayerStatDescriptions()
    }

    fun startGameForPlayer(name: String, request: PlayerStartGameRequest) {
        val user = userDomainService.getUserByName(name)!!
        val player : PlayerResponse = playerDomainService.getPlayer(user.id, request.playerId)

        incomingEventBus.pushEvent(incomingEventBus.createEvent(user.id, player.id, player.locationId, player.name, player.classId, 1))
    }

    fun getPlayerById(playerId: Long) : PlayerResponse {

        return playerDomainService.getPlayer(playerId)
    }
}