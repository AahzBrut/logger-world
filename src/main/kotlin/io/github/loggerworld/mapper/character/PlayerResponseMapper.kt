package io.github.loggerworld.mapper.character

import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class PlayerResponseMapper : Mapper<PlayerResponse, Player> {

    override fun from(source: Player): PlayerResponse {

        return PlayerResponse(
            source.id!!,
            source.userAccount.id!!,
            source.name,
            source.playerClass.id!!,
            source.location.id!!
        )
    }
}