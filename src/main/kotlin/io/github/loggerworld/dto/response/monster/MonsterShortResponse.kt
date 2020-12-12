package io.github.loggerworld.dto.response.monster

import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes

data class MonsterShortResponse(
    var id: Long = -1,
    var level: Byte = -1,
    var monsterClass: MonsterClasses = MonsterClasses.GREY_RAT,
    var monsterType: MonsterTypes = MonsterTypes.NORMAL,
)
