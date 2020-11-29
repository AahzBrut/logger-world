package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerClassLevelStats
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerClassInitialStatsRepository: JpaRepository<PlayerClassLevelStats, Short> {

    fun findAllByPlayerClassIdAndLevel(classId: Byte, level: Byte) : List<PlayerClassLevelStats>
}