package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.request.PlayerMoveRequest
import io.github.loggerworld.dto.request.PlayerStartGameRequest
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayerStatResponse
import io.github.loggerworld.dto.response.character.PlayerStatsResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.messagebus.CommandEventBus
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import io.github.loggerworld.service.domain.PlayerClassDomainService
import io.github.loggerworld.service.domain.PlayerDomainService
import io.github.loggerworld.service.domain.UserDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.socket.messaging.SessionDisconnectEvent

typealias PlayerClassDescriptionsMap = Map<PlayerClasses, Map<Languages, Pair<String, String>>>
typealias PlayerStatDescriptionsMap = Map<PlayerStatEnum, Map<Languages, Pair<String, String>>>


@Service
class PlayerService(
    private val playerDomainService: PlayerDomainService,
    private val playerClassDomainService: PlayerClassDomainService,
    private val userDomainService: UserDomainService,
    private val startEventBus: CommandEventBus<PlayerStartCommand>,
    private val moveEventBus: CommandEventBus<PlayerMoveCommand>,
) : LogAware {

    private val activePlayers: MutableMap<Long, Long> = mutableMapOf()

    fun getAllPlayers(userName: String): PlayersResponse {
        val user = userDomainService.getUserByName(userName)!!
        return PlayersResponse(playerDomainService.getAllPlayers(user.id, user.language))
    }

    fun getAllClasses(userName: String): PlayerClassesResponse {
        val user = userDomainService.getUserByName(userName)!!
        val response = PlayerClassesResponse(playerDomainService.getAllPlayerClasses(user.id, user.language))
        response.playerClasses.forEach {
            it.stats = playerClassDomainService.getInitialStatsForPlayerClass(it.id)
        }
        return response
    }

    fun addNewPlayer(userName: String, request: PlayerAddRequest): PlayerResponse {
        val user = userDomainService.getUserByName(userName)!!

        val classStats = playerClassDomainService.getInitialStatsForPlayerClass(request.playerClass.ordinal.toByte())

        val playerId = playerDomainService.addNewPlayer(user.id, request, classStats)


        return playerDomainService.getPlayer(playerId)
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

        if (activePlayers.containsKey(user.id)) {
            if (activePlayers[user.id] != request.playerId) {
                error("User already have active player in the game. To proceed with new player you must logoff other player.")
            }
        }

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

    fun getPlayerStats(userLanguage: Languages): PlayerStatsResponse {

        return PlayerStatsResponse(
            getAllPlayerStatDescriptions().entries.map {
                PlayerStatResponse(
                    it.key.ordinal.toByte(),
                    it.key.name,
                    requireNotNull(it.value[userLanguage]).first,
                    requireNotNull(it.value[userLanguage]).second
                )
            }.toList())
    }

    @EventListener
    fun onDisconnectEvent(event: SessionDisconnectEvent) {
        logger().debug("Socket closed for user:${(event.user ?: "null")} with status:${event.closeStatus}")
        event.user?.let {
            val disconnectedUser = userDomainService.getUserByName(it.name)
            disconnectedUser?.let {
                activePlayers.remove(disconnectedUser.id)
                logger().debug("Active players:$activePlayers")
            }
        }
    }
}