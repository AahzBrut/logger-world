package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class EquipmentSlotTypes {
    NOTHING,
    HEAD,
    AMULET,
    LEFT_EARRING,
    RIGHT_EARRING,
    BODY,
    LEFT_ARM,
    RIGHT_ARM,
    GLOVES,
    LEFT_RING,
    RIGHT_RING,
    QUIVER,
    BELT,
    PANTS,
    BOOTS;

    @JsonValue
    fun jsonValue() = ordinal
}