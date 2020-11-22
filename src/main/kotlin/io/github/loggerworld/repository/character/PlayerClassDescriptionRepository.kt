package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerClassDescription
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerClassDescriptionRepository : JpaRepository<PlayerClassDescription, Short>