package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class ItemStatEnum {
    NONE,
    DURABILITY,
    MAX_DURABILITY,
    USES_PER_DURABILITY,
    WEIGHT,
    MIN_DAMAGE,
    MAX_DAMAGE,
    ARMOR,
    STACK_SIZE;

    @JsonValue
    fun jsonValue() = ordinal
}