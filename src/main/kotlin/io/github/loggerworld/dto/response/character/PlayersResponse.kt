package io.github.loggerworld.dto.response.character

data class PlayersResponse (
    var players: List<PlayerResponse> = emptyList()
)