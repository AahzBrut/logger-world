package io.github.loggerworld.repository.loot

import io.github.loggerworld.domain.loot.LootDrop
import org.springframework.data.jpa.repository.JpaRepository

interface LootDropRepository : JpaRepository<LootDrop, Short>
