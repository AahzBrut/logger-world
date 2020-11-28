package io.github.loggerworld.service.domain

import io.github.loggerworld.repository.character.PlayerClassInitialStatsRepository
import org.springframework.stereotype.Service

@Service
class PlayerClassDomainService(
    private val playerClassInitialStatsRepository: PlayerClassInitialStatsRepository
) {

    fun getInitialStatsForPlayerClass(classId: Byte): Map<Byte, Int> {
        return playerClassInitialStatsRepository
            .findAllByPlayerClassId(classId)
            .associate {
                it.playerStat.id!! to it.value
            }
    }
}