package io.github.loggerworld.dto.request.commands

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerStartGameRequest(
    @JsonProperty("1")
    var playerId: Long = -1
)
