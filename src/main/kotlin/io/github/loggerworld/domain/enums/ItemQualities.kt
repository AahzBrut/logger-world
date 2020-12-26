package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class ItemQualities {
    NONE,
    COMMON,
    GOOD,
    RARE,
    EPIC,
    LEGENDARY;

    @JsonValue
    fun jsonValue() = ordinal
}