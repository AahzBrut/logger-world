package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.mapper.PlayerLogEntryMapper
import io.github.loggerworld.messagebus.event.DepartureEvent
import org.springframework.stereotype.Service

@Service
class LogEntryFromDepartureEventMapper: PlayerLogEntryMapper<DepartureEvent> {

    override fun from(
        source: DepartureEvent,
        message: String,
        valueDecoders: Map<LogValueTypes, (String, Languages) -> String>,
        language: Languages
    ): PlayerLogEntryResponse {

        return PlayerLogEntryResponse(
            source.created,
            source.eventType.logClass,
            source.eventType,
            message.format(
                valueDecoders[LogValueTypes.PLAYER_ID]!!.invoke(source.playerId.toString(), language),
                valueDecoders[LogValueTypes.LOCATION_ID]!!.invoke(source.fromLocationId.toString(), language),
                valueDecoders[LogValueTypes.LOCATION_ID]!!.invoke(source.toLocationId.toString(), language),
            )
        )
    }
}