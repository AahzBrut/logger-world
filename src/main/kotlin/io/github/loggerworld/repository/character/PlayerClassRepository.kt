package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerClass
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerClassRepository : JpaRepository<PlayerClass, Byte>