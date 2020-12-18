package io.github.loggerworld.repository.character

import io.github.loggerworld.domain.character.PlayerStatDependency
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerStatDependencyRepository: JpaRepository<PlayerStatDependency, Short>
