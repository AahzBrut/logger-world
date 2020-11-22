package io.github.loggerworld.mapper.character

import io.github.loggerworld.domain.character.PlayerClassDescription
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.dto.inner.PlayerClassDescriptionInner
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service


@Service
class PlayerClassDescriptionInnerMapper : Mapper<PlayerClassDescriptionInner, PlayerClassDescription> {

    override fun from(source: PlayerClassDescription): PlayerClassDescriptionInner {
        return PlayerClassDescriptionInner(
            PlayerClasses.getById(source.playerClass.id!!),
            Languages.getById(source.language.id!!),
            source.name,
            source.description
        )
    }
}