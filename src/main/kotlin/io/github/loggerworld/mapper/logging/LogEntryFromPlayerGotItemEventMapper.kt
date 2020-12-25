package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.mapper.PlayerLogEntryMapper
import io.github.loggerworld.messagebus.event.PlayerGotItemEvent
import org.springframework.stereotype.Service

@Service
class LogEntryFromPlayerGotItemEventMapper: PlayerLogEntryMapper<PlayerGotItemEvent> {

    override fun from(
        source: PlayerGotItemEvent,
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
                valueDecoders[LogValueTypes.ITEM_ID]!!.invoke("${source.itemData.category.ordinal},${source.itemData.quality.ordinal},${source.itemData.quantity}", language),
            )
        )
    }
}