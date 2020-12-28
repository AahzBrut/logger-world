package io.github.loggerworld.dto.response.monster

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.loggerworld.domain.enums.MonsterClasses

data class MobNestResponse(
    @JsonProperty("1")
    var id: Short = 0,
    @JsonProperty("1")
    var mobClass: MonsterClasses = MonsterClasses.GREY_RAT,
    @JsonProperty("1")
    var level: Byte = 0,
    @JsonProperty("1")
    var amount: Short = 0
)
