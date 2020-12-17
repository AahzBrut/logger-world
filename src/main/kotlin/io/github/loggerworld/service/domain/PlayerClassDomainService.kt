package io.github.loggerworld.service.domain

import io.github.loggerworld.repository.character.PlayerClassInitialAttributesRepository
import io.github.loggerworld.repository.character.PlayerClassInitialStatsRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PlayerClassDomainService(
    private val playerClassInitialStatsRepository: PlayerClassInitialStatsRepository,
    private val playerClassInitialAttributesRepository: PlayerClassInitialAttributesRepository,
) {

    @Transactional
    fun getInitialStatsForPlayerClass(classId: Byte): Map<Byte, Float> {
        return playerClassInitialStatsRepository
            .findAllByPlayerClassIdAndLevel(classId, 1)
            .associate {
                it.playerStat.id!! to it.value
            }
    }

    @Transactional
    fun getInitialAttributesForPlayerClass(classId: Byte): Map<Byte, Float> {
        return playerClassInitialAttributesRepository
            .findAllByPlayerClassIdAndLevel(classId, 1)
            .associate {
                it.playerAttribute.id!! to it.value
            }
    }
}