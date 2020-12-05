package io.github.loggerworld.dto.response.character

import io.github.loggerworld.ecs.component.MoveStates

data class ShortPlayerResponse(
    var id: Long = -1,
    var name: String = "",
    var level: Byte = -1,
    var classId: Byte = -1,
    var moveState: MoveStates = MoveStates.STATIONARY
)
