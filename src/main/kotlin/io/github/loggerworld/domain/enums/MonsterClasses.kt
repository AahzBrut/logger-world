package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class MonsterClasses {
    NOTHING,
    GREY_RAT,
    POISONOUS_ADDER,
    BEE_HIVE,
    GREEN_SLIME,
    UNBORN_CHICKEN,
    GRIZZLY_BEAR;

    @JsonValue
    fun jsonValue() = ordinal
}