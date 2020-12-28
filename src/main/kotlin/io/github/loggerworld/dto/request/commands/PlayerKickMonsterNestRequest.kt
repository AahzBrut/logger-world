package io.github.loggerworld.dto.request.commands

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerKickMonsterNestRequest(
    @JsonProperty("1")
    var nestId: Short
)
