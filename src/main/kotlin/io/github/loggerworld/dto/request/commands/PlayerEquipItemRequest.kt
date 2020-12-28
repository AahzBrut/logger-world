package io.github.loggerworld.dto.request.commands

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.loggerworld.domain.enums.EquipmentSlotTypes

data class PlayerEquipItemRequest(
    @JsonProperty("1")
    var itemId: Long,
    @JsonProperty("2")
    var slotType: EquipmentSlotTypes
)
