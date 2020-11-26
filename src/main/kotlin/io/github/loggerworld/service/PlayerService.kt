package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.request.PlayerMoveRequest
import io.github.loggerworld.dto.request.PlayerStartGameRequest
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.messagebus.CommandEventBus
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import io.github.loggerworld.service.domain.PlayerDomainService
import io.github.loggerworld.service.domain.UserDomainService
import org.springframework.stereotype.Service

typealias PlayerClassDescriptionsMap = Map<PlayerClasses, Map<Languages, Pair<String, String>>>
typealias PlayerStatDescriptionsMap = Map<PlayerStatEnum, Map<Languages, Pair<String, String>>>


@Service
class PlayerService(
    private val playerDomainService: PlayerDomainService,
    private val userDomainService: UserDomainService,
    private val startEventBus: CommandEventBus<PlayerStartCommand>,
    private val moveEventBus: CommandEventBus<PlayerMoveCommand>,
) {
    private val activePlayers: MutableMap<Long, Long> = mutableMapOf()

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

    fun getAllPlayerStatDescriptions(): PlayerStatDescriptionsMap {

        return playerDomainService.getAllPlayerStatDescriptions()
    }

    fun startGameForPlayer(name: String, request: PlayerStartGameRequest) {
        val user = userDomainService.getUserByName(name)!!
        val player: PlayerResponse = playerDomainService.getPlayer(user.id, request.playerId)

        if (activePlayers.containsKey(user.id)) error("User already have active player in game.")

        activePlayers[user.id] = player.id

        startEventBus.pushEvent(startEventBus.newEvent().also {
            it.userId = user.id
            it.playerId = player.id
            it.locationId = player.locationId
            it.name = player.name
            it.classId = player.classId
            it.level = 1
        })
    }

    fun getPlayerById(playerId: Long): PlayerResponse {

        return playerDomainService.getPlayer(playerId)
    }

    fun movePlayer(name: String, request: PlayerMoveRequest) {
        val user = userDomainService.getUserByName(name)!!

        if (!activePlayers.containsKey(user.id)) error("User have no active player.")

        val moveEvent = moveEventBus.newEvent().also {
            it.locationId = request.locationId
            it.playerId = activePlayers[user.id]!!
        }

        moveEventBus.pushEvent(moveEvent)
    }
}