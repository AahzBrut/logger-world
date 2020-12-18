package io.github.loggerworld.dto.inner

import io.github.loggerworld.domain.enums.CalcTypes
import io.github.loggerworld.domain.enums.PlayerAttributeEnum
import io.github.loggerworld.domain.enums.PlayerStatEnum

data class PlayerEffectiveStatData(
    val stats: Map<PlayerStatEnum, Map<PlayerAttributeEnum, StatAttributeDependency>>
)

data class StatAttributeDependency(
    val coeff: Float,
    val formula: CalcTypes
)