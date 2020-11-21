package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.character.PlayerClass
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.geography.Location
import io.github.loggerworld.domain.user_account.UserAccount
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.response.character.PlayerClassResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.mapper.Mapper
import io.github.loggerworld.mapper.character.PlayerClassResponseMapper
import io.github.loggerworld.repository.character.PlayerClassRepository
import io.github.loggerworld.repository.character.PlayerRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PlayerDomainService(
    private val playerRepository: PlayerRepository,
    private val playerClassRepository: PlayerClassRepository,
    private val playerResponseMapper: Mapper<PlayerResponse, Player>,
    private val playerClassResponseMapper: PlayerClassResponseMapper
) {

    @Transactional
    fun getAllPlayers(userId: Long, lang: Languages): List<PlayerResponse> {
        val userPlayers = playerRepository.findAllByUserAccountId(userId)
        return playerResponseMapper.from(userPlayers)
    }

    @Transactional
    fun getAllPlayerClasses(userId: Long, lang: Languages): List<PlayerClassResponse> {
        val playerClasses = playerClassRepository.findAll()
        return playerClassResponseMapper.from(playerClasses)
    }

    @Transactional
    fun addNewPlayer(userId: Long, request: PlayerAddRequest) {
        val player = Player(
            UserAccount().also { it.id = userId },
            PlayerClass(request.playerClass),
            Location().also { it.id = 6 },
            request.name)

        playerRepository.save(player)
    }
}