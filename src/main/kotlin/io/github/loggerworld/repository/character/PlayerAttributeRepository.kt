package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerAttribute
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerAttributeRepository : JpaRepository<PlayerAttribute, Byte>
