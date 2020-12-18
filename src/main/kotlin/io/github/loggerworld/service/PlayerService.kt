package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.request.commands.PlayerKickMonsterNestRequest
import io.github.loggerworld.dto.request.commands.PlayerMoveRequest
import io.github.loggerworld.dto.request.commands.PlayerStartGameRequest
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayerStatResponse
import io.github.loggerworld.dto.response.character.PlayerStatsResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.dto.response.user.UserResponse
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.LogoffEvent
import io.github.loggerworld.messagebus.event.PlayerKickMonsterNestCommand
import io.github.loggerworld.messagebus.event.PlayerMoveCommand
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import io.github.loggerworld.service.domain.PlayerAttributeDomainService
import io.github.loggerworld.service.domain.PlayerClassDomainService
import io.github.loggerworld.service.domain.PlayerDomainService
import io.github.loggerworld.service.domain.UserDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

typealias PlayerClassDescriptionsMap = Map<PlayerClasses, Map<Languages, Pair<String, String>>>
typealias PlayerStatDescriptionsMap = Map<PlayerStatEnum, Map<Languages, Pair<String, String>>>


@Service
class PlayerService(
    private val playerDomainService: PlayerDomainService,
    private val playerClassDomainService: PlayerClassDomainService,
    private val userDomainService: UserDomainService,
    private val startEventBus: EventBus<PlayerStartCommand>,
    private val moveEventBus: EventBus<PlayerMoveCommand>,
    private val kickNestEventBus: EventBus<PlayerKickMonsterNestCommand>,
    private val logEventBus: LogEventBus<LogEvent>,
    private val playerAttributeDomainService: PlayerAttributeDomainService,
) : LogAware {

    private val activePlayers: MutableMap<Long, Long> = ConcurrentHashMap()

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
        val classAttributes = playerClassDomainService.getInitialAttributesForPlayerClass(request.playerClass.ordinal.toByte())

        val playerId = playerDomainService.addNewPlayer(user.id, request, classStats, classAttributes)

        return getPlayerById(playerId)
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

        if (activePlayers.containsKey(user.id) && activePlayers[user.id] != request.playerId) {
            error("User already have active player in the game. To proceed with new player you must logoff other player.")
        }

        activePlayers[user.id] = player.id

        startEventBus.dispatchEvent {
            it.userId = user.id
            it.playerId = player.id
            it.locationId = player.locationId
            it.name = player.name
            it.classId = player.classId
            it.level = 1
        }
    }

    fun getPlayerById(playerId: Long): PlayerResponse {

        val player = playerDomainService.getPlayer(playerId)
        player.effectiveAttributes = player.baseAttributes
        player.effectiveStats = playerAttributeDomainService.getEffectiveStats(player.baseStats, player.effectiveAttributes)

        return player
    }

    fun getUserByPlayerId(playerId: Long): UserResponse {

        return userDomainService.getUserById(playerDomainService.getPlayer(playerId).userId)
    }

    fun movePlayer(name: String, request: PlayerMoveRequest) {
        val user = userDomainService.getUserByName(name)!!

        if (!activePlayers.containsKey(user.id)) error("User have no active player.")

        moveEventBus.dispatchEvent {
            it.locationId = request.locationId
            it.playerId = activePlayers[user.id]!!
        }
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
            }.toList()
        )
    }

    fun getActivePlayer(userId: Long): Long {

        return activePlayers[userId]!!
    }

    fun isPlayerUserOnline(playerId: Long): Boolean {
        return activePlayers.containsValue(playerId)
    }

    @EventListener
    fun onDisconnectEvent(event: SessionDisconnectEvent) {
        logger().debug("Socket closed for user:${(event.user ?: "null")} with status:${event.closeStatus}")
        event.user?.let {
            val disconnectedUser = userDomainService.getUserByName(it.name)
            disconnectedUser?.let {
                val loginEvent = logEventBus.newEvent(LogoffEvent::class) as LogoffEvent
                loginEvent.playerId = activePlayers[disconnectedUser.id]!!
                loginEvent.created = OffsetDateTime.now()
                logEventBus.pushEvent(loginEvent)

                activePlayers.remove(disconnectedUser.id)
                logger().debug("\n\nUser: ${disconnectedUser.loginName} disconnected")
                logger().debug("Active players:$activePlayers")
            }
        }
    }

    fun decodePlayer(id: String, language: Languages): String {

        return playerDomainService.getPlayer(id.toLong()).name
    }

    fun kickMonsterNest(userName: String, request: PlayerKickMonsterNestRequest) {

        val playerId = getActivePlayer(userDomainService.getUserByName(userName)!!.id)

        kickNestEventBus.dispatchEvent {
            it.playerId = activePlayers[playerId]!!
            it.monsterNestId = request.nestId
        }
    }
}