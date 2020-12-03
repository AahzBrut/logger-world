package io.github.loggerworld.dto.response

import io.github.loggerworld.domain.enums.MonsterClasses

data class MobNestResponse(
    var id: Short = 0,
    var mobClass: MonsterClasses = MonsterClasses.GREY_RAT,
    var level: Byte = 0,
    var amount: Short = 0
)
