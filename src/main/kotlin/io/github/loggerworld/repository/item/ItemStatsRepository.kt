package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.ItemStats
import org.springframework.data.jpa.repository.JpaRepository

interface ItemStatsRepository : JpaRepository<ItemStats, Long>
