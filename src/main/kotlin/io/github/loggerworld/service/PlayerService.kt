package io.github.loggerworld.service

import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.service.domain.PlayerDomainService
import io.github.loggerworld.service.domain.UserDomainService
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val playerDomainService: PlayerDomainService,
    private val userDomainService: UserDomainService
) {

    fun getAllPlayers(userName: String): PlayersResponse {
        val user = userDomainService.getUserByName(userName)!!
        return PlayersResponse(playerDomainService.getAllPlayers(user.id, user.language))
    }

    fun getAllClasses(userName: String): PlayerClassesResponse {
        val user = userDomainService.getUserByName(userName)!!
        return PlayerClassesResponse(playerDomainService.getAllPlayerClasses(user.id, user.language))
    }
}