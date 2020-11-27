package io.github.loggerworld.dto.request

import io.github.loggerworld.domain.enums.PlayerClasses

data class PlayerAddRequest (
    var name: String = "",
    var playerClass: PlayerClasses = PlayerClasses.DUMMY
)
