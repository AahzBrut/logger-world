package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerClassLevelAttributes
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerClassInitialAttributesRepository : JpaRepository<PlayerClassLevelAttributes, Short> {

    fun findAllByPlayerClassIdAndLevel(classId: Byte, level: Byte) : List<PlayerClassLevelAttributes>
}
