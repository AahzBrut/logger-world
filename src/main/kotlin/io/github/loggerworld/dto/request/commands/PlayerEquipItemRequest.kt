package io.github.loggerworld.dto.request.commands

import io.github.loggerworld.domain.enums.EquipmentSlotTypes

data class PlayerEquipItemRequest(
    var itemId: Long,
    var slotType: EquipmentSlotTypes
)
