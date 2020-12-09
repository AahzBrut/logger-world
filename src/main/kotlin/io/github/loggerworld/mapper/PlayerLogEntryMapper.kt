package io.github.loggerworld.mapper

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse

interface PlayerLogEntryMapper <S> {

    fun from(source: S, message: String, valueDecoders: Map<LogValueTypes, (String, Languages) -> String>, language: Languages) : PlayerLogEntryResponse
}