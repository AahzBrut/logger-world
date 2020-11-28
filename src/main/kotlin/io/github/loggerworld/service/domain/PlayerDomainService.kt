package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.character.PlayerStat
import io.github.loggerworld.domain.character.PlayerStats
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.response.character.PlayerClassResponse
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.exception.PlayerCreateException
import io.github.loggerworld.mapper.character.PlayerClassDescriptionInnerMapper
import io.github.loggerworld.mapper.character.PlayerClassResponseMapper
import io.github.loggerworld.mapper.character.PlayerResponseMapper
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
    private val playerResponseMapper: PlayerResponseMapper,
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
    fun addNewPlayer(userId: Long, request: PlayerAddRequest, classStats: Map<Byte, Int>): Long {

        checkRequestStats(request, classStats)

        val player = Player().also { newPlayer ->
            newPlayer.userAccount.id = userId
            newPlayer.playerClass.id = request.playerClass.ordinal.toByte()
            newPlayer.location.id = 6
            newPlayer.name = request.name
            newPlayer.playerStats.addAll(
                classStats.entries.map {
                    PlayerStats(
                        newPlayer,
                        PlayerStat(PlayerStatEnum.getById(it.key)),
                        if (PlayerStatEnum.getById(it.key) == PlayerStatEnum.UNALLOCATED_POINTS)
                            getPointsLeft(request.statPoints, classStats)
                        else
                            (request.statPoints[it.key]
                                ?: 0) + it.value)
                }
            )
        }

        return requireNotNull(playerRepository.save(player).id)
    }

    private fun getPointsLeft(points: Map<Byte, Int>, classStats: Map<Byte, Int>): Int {
        val maxPoints: Int = requireNotNull(classStats[PlayerStatEnum.POINTS_ON_LEVELUP.ordinal.toByte()])
        return maxPoints - points.entries
            .sumOf {
                it.value
            }
    }

    private fun checkRequestStats(request: PlayerAddRequest, classStats: Map<Byte, Int>) {
        val maxPoints: Int = requireNotNull(classStats[PlayerStatEnum.POINTS_ON_LEVELUP.ordinal.toByte()])
        val anyNegativeStats = request.statPoints.entries.filter { PlayerStatEnum.getById(it.key).isEditable }.any { it.value < 0 }
        val anyWrongStats = request.statPoints.entries.any { !PlayerStatEnum.getById(it.key).isEditable }
        val sumOfPoints = request.statPoints.entries
            .sumOf {
                it.value
            }

        if (sumOfPoints > maxPoints ||
            sumOfPoints < 0 ||
            anyNegativeStats ||
            anyWrongStats) throw PlayerCreateException("Invalid parameters of character creation!")

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

    @Transactional
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

    @Transactional
    fun getPlayer(userId: Long, playerId: Long): PlayerResponse {

        val player = playerRepository.findByUserAccountIdAndId(userId, playerId)
            ?: error("There is no player with id:$playerId which belong to user with id: $userId")

        return playerResponseMapper.from(player)
    }

    @Transactional
    fun getPlayer(playerId: Long): PlayerResponse {

        val player = playerRepository.getOne(playerId)

        return playerResponseMapper.from(player)
    }
}