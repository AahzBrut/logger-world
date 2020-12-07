package io.github.loggerworld.dto.response.logging

data class PlayerLogsResponse(
    var entries: MutableList<PlayerLogEntryResponse> = mutableListOf()
)
