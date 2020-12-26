package io.github.loggerworld.repository.equipment

import io.github.loggerworld.domain.equipment.EquipmentSlotTypeDescription
import org.springframework.data.jpa.repository.JpaRepository

interface EquipmentSlotTypeDescriptionRepository : JpaRepository<EquipmentSlotTypeDescription, Short>
