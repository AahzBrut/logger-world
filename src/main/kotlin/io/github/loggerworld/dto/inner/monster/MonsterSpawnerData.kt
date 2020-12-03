package io.github.loggerworld.dto.inner.monster

import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.domain.enums.PlayerStatEnum

data class MonsterClassSpawnerData(
    var classes: MutableMap<MonsterClasses, MonsterLevelSpawnerData> = mutableMapOf()
)

data class MonsterLevelSpawnerData(
    var levels: MutableMap<Byte, MonsterTypeSpawnerData> = mutableMapOf()
)

data class MonsterTypeSpawnerData(
    var types: MutableMap<MonsterTypes, MonsterSpawnerData> = mutableMapOf(),
    var probabilities: MutableMap<MonsterTypes, Double> = mutableMapOf()
)

data class MonsterSpawnerData(
    var stats: MutableMap<PlayerStatEnum, Double> = mutableMapOf()
)