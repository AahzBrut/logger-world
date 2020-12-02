package io.github.loggerworld.dto.inner.monster

import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.domain.enums.PlayerStatEnum

data class MonsterClassSpawnerData(
    var classes: Map<MonsterClasses, MonsterLevelSpawnerData>
)

data class MonsterLevelSpawnerData(
    var levels: Map<Byte, MonsterTypeSpawnerData>
)

data class MonsterTypeSpawnerData(
    var types: Map<MonsterTypes, MonsterSpawnerData>,
    var probabilities: Map<MonsterTypes, Int>
)

data class MonsterSpawnerData(
    var stats: Map<PlayerStatEnum, Int>
)