package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerClassInitialStats
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerClassInitialStatsRepository: JpaRepository<PlayerClassInitialStats, Short> {

    fun findAllByPlayerClassId(classId: Byte) : List<PlayerClassInitialStats>
}