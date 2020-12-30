package io.github.loggerworld.repository.loot

import io.github.loggerworld.domain.loot.LootDropCategory
import org.springframework.data.jpa.repository.JpaRepository

interface LootDropCategoryRepository : JpaRepository<LootDropCategory, Short>
