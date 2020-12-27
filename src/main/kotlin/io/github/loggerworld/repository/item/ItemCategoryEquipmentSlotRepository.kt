package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.ItemCategoryEquipmentSlot
import org.springframework.data.jpa.repository.JpaRepository

interface ItemCategoryEquipmentSlotRepository : JpaRepository<ItemCategoryEquipmentSlot, Short>
