package io.github.loggerworld.dto.inner.monster

import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.domain.enums.PlayerStatEnum

data class MonsterData(
    var monsterClass: MonsterClasses = MonsterClasses.GREY_RAT,
    var monsterType: MonsterTypes = MonsterTypes.NORMAL,
    var level: Byte = 0,
    var stats: MutableMap<PlayerStatEnum, Int> = mutableMapOf()
)
