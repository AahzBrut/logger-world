package io.github.loggerworld.mapper.character

import io.github.loggerworld.domain.character.PlayerStatDescription
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.dto.inner.PlayerStatDescriptionInner
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class PlayerStatDescriptionInnerMapper : Mapper<PlayerStatDescriptionInner, PlayerStatDescription> {

    override fun from(source: PlayerStatDescription): PlayerStatDescriptionInner {

        return PlayerStatDescriptionInner(
            PlayerStatEnum.getById(source.playerStat.id!!),
            Languages.getById(source.language.id!!),
            source.name,
            source.description
        )
    }
}