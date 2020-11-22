package io.github.loggerworld.dto.inner

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerStatEnum

data class PlayerStatDescriptionInner(
    var playerStat: PlayerStatEnum,
    var language: Languages,
    var name: String,
    var description: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayerStatDescriptionInner) return false

        if (playerStat != other.playerStat) return false

        return true
    }

    override fun hashCode(): Int {
        return playerStat.hashCode()
    }
}