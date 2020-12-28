package io.github.loggerworld.dto.response.monster

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes

data class MonsterShortResponse(
    @JsonProperty("1")
    var id: Long = -1,
    @JsonProperty("2")
    var level: Byte = -1,
    @JsonProperty("3")
    var monsterClass: MonsterClasses = MonsterClasses.GREY_RAT,
    @JsonProperty("4")
    var monsterType: MonsterTypes = MonsterTypes.NORMAL,
)
