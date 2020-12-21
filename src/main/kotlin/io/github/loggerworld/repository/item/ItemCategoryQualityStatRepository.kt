package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.ItemCategoryQualityStat
import org.springframework.data.jpa.repository.JpaRepository

interface ItemCategoryQualityStatRepository : JpaRepository<ItemCategoryQualityStat, Short>
