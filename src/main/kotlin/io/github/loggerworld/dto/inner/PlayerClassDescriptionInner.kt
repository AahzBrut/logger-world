package io.github.loggerworld.dto.inner

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses

data class PlayerClassDescriptionInner(
    var playerClass: PlayerClasses,
    var language: Languages,
    var name: String,
    var description: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayerClassDescriptionInner) return false

        if (playerClass != other.playerClass) return false

        return true
    }

    override fun hashCode(): Int {
        return playerClass.hashCode()
    }
}