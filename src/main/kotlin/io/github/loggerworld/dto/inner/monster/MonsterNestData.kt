package io.github.loggerworld.dto.inner.monster

import io.github.loggerworld.domain.enums.MonsterClasses

data class MonsterNestData(
    var id: Short = Short.MIN_VALUE,
    var monsterClass: MonsterClasses = MonsterClasses.GREY_RAT,
    var level: Byte = 0,
    var amount: Short = 0,
    var minRespawnTime: Double = 0.0,
    var maxRespawnTime: Double = 0.0,
)
