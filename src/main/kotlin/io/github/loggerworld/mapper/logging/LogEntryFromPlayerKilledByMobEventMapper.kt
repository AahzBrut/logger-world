package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogValueTypes
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.mapper.PlayerLogEntryMapper
import io.github.loggerworld.messagebus.event.PlayerKilledByMobEvent
import org.springframework.stereotype.Service

@Service
class LogEntryFromPlayerKilledByMobEventMapper : PlayerLogEntryMapper<PlayerKilledByMobEvent> {

    override fun from(
        source: PlayerKilledByMobEvent,
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
                valueDecoders[LogValueTypes.MONSTER_ID]!!.invoke(source.monsterName, language),
                valueDecoders[LogValueTypes.DAMAGE_AMOUNT]!!.invoke(source.damageDealt.toString(), language),
                valueDecoders[LogValueTypes.DAMAGE_AMOUNT]!!.invoke(source.damageReceived.toString(), language),
            )
        )
    }
}