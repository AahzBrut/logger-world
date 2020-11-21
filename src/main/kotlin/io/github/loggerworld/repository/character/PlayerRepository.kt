package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.Player
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository : JpaRepository<Player, Long> {

    fun findAllByUserAccountId(userId: Long) : List<Player>
}