package io.github.loggerworld.dto.response.monster

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.loggerworld.domain.enums.MonsterClasses

data class MobNestResponse(
    @JsonProperty("1")
    var id: Short = 0,
    @JsonProperty("2")
    var mobClass: MonsterClasses = MonsterClasses.GREY_RAT,
    @JsonProperty("3")
    var level: Byte = 0,
    @JsonProperty("4")
    var amount: Short = 0
)
