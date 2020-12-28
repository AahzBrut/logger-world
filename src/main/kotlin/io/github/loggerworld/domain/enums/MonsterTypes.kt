package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class MonsterTypes {
    NOTHING,
    NORMAL,
    VETERAN,
    ELITE;

    @JsonValue
    fun jsonValue() = ordinal
}