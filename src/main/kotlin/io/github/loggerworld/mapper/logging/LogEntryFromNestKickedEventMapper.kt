package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.mapper.PlayerLogEntryMapper
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.messagebus.event.NestKickEvent
import org.springframework.stereotype.Service

@Service
class LogEntryFromNestKickedEventMapper : PlayerLogEntryMapper<NestKickEvent> {

    override fun from(
        source: NestKickEvent,
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
                valueDecoders[LogValueTypes.MONSTER_NEST_ID]!!.invoke(source.nestId.toString(), language),
            )
        )

    }
}