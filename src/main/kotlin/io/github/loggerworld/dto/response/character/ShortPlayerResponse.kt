package io.github.loggerworld.dto.response.character

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.loggerworld.ecs.component.States

data class ShortPlayerResponse(
    @JsonProperty("1")
    var id: Long = -1,
    @JsonProperty("2")
    var name: String = "",
    @JsonProperty("3")
    var level: Byte = -1,
    @JsonProperty("4")
    var classId: Byte = -1,
    @JsonProperty("5")
    var state: States = States.IDLE
)
