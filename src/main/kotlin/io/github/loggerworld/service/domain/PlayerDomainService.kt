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
import io.github.loggerworld.mapper.character.PlayerClassDescriptionInnerMapper
import io.github.loggerworld.mapper.character.PlayerClassResponseMapper
import io.github.loggerworld.mapper.character.PlayerStatDescriptionInnerMapper
import io.github.loggerworld.repository.character.PlayerClassDescriptionRepository
import io.github.loggerworld.repository.character.PlayerClassRepository
import io.github.loggerworld.repository.character.PlayerRepository
import io.github.loggerworld.repository.character.PlayerStatDescriptionRepository
import io.github.loggerworld.service.PlayerClassDescriptionsMap
import io.github.loggerworld.service.PlayerStatDescriptionsMap
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PlayerDomainService(
    private val playerRepository: PlayerRepository,
    private val playerClassRepository: PlayerClassRepository,
    private val playerClassDescriptionRepository: PlayerClassDescriptionRepository,
    private val playerStatDescriptionRepository: PlayerStatDescriptionRepository,
    private val playerResponseMapper: Mapper<PlayerResponse, Player>,
    private val playerClassResponseMapper: PlayerClassResponseMapper,
    private val playerClassDescriptionInnerMapper: PlayerClassDescriptionInnerMapper,
    private val playerStatDescriptionInnerMapper: PlayerStatDescriptionInnerMapper,
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

    @Transactional
    fun getAllPlayerClassDescriptions(): PlayerClassDescriptionsMap {
        val allDescriptions = playerClassDescriptionRepository.findAll()

        return playerClassDescriptionInnerMapper
            .from(allDescriptions)
            .groupBy { it.playerClass }
            .mapValues { entry ->
                entry.value.associate {
                    it.language to Pair(it.name, it.description)
                }
            }
    }

    fun getAllPlayerStatDescriptions(): PlayerStatDescriptionsMap {
        val allStatDescriptions = playerStatDescriptionRepository.findAll()

        return playerStatDescriptionInnerMapper
            .from(allStatDescriptions)
            .groupBy { it.playerStat }
            .mapValues { entry ->
                entry.value.associate {
                    it.language to Pair(it.name, it.description)
                }
            }
    }

    fun getPlayer(userId: Long, playerId: Long): PlayerResponse {

        val player = playerRepository.findByUserAccountIdAndId(userId, playerId) ?: error("There is no player with id:$playerId which belong to user with id: $userId")

        return playerResponseMapper.from(player)
    }

    fun getPlayer(playerId: Long): PlayerResponse {

        val player = playerRepository.getOne(playerId)

        return playerResponseMapper.from(player)
    }
}