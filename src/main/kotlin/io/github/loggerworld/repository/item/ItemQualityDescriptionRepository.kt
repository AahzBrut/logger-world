package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.ItemQualityDescription
import org.springframework.data.jpa.repository.JpaRepository

interface ItemQualityDescriptionRepository: JpaRepository<ItemQualityDescription, Short>
