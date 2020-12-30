package io.github.loggerworld.repository.loot

import io.github.loggerworld.domain.loot.LootDropQuality
import org.springframework.data.jpa.repository.JpaRepository

interface LootDropQualityRepository : JpaRepository<LootDropQuality, Short>
