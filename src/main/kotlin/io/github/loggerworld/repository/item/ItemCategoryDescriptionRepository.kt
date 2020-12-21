package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.ItemCategoryDescription
import org.springframework.data.jpa.repository.JpaRepository

interface ItemCategoryDescriptionRepository: JpaRepository<ItemCategoryDescription, Short>
