package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerStat
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerStatRepository: JpaRepository<PlayerStat, Byte>