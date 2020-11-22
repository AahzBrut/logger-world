package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerStatDescription
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerStatDescriptionRepository : JpaRepository<PlayerStatDescription, Short>