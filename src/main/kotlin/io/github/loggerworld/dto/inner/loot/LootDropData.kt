package io.github.loggerworld.dto.inner.loot

import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes

data class LootDropData(
    val monsterClass: MonsterClasses,
    val monsterType: MonsterTypes,
    val level: Byte
)
