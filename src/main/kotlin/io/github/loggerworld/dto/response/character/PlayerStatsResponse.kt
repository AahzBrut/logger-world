package io.github.loggerworld.dto.response.character

data class PlayerStatsResponse(
    var stats: List<PlayerStatResponse> = emptyList()
)
