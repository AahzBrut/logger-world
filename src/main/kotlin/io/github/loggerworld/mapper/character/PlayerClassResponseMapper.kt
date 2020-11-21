package io.github.loggerworld.mapper.character

import io.github.loggerworld.domain.character.PlayerClass
import io.github.loggerworld.dto.response.character.PlayerClassResponse
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class PlayerClassResponseMapper : Mapper<PlayerClassResponse, PlayerClass> {

    override fun from(source: PlayerClass): PlayerClassResponse {
        return PlayerClassResponse(
            source.id!!,
            source.code.name
        )
    }
}