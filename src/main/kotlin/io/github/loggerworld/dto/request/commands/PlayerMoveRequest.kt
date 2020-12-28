package io.github.loggerworld.dto.request.commands

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerMoveRequest(
    @JsonProperty("1")
    var locationId: Short
)
