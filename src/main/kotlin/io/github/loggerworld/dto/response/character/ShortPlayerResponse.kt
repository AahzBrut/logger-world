package io.github.loggerworld.dto.response.character

import io.github.loggerworld.ecs.component.States

data class ShortPlayerResponse(
    var id: Long = -1,
    var name: String = "",
    var level: Byte = -1,
    var classId: Byte = -1,
    var state: States = States.IDLE
)
