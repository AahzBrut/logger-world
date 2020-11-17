package io.github.loggerworld.dto.request

import io.github.loggerworld.domain.enums.Languages

data class UserAddRequest(
    var userName: String = "",
    var password: String = "",
    var language: Languages = Languages.EN,
    var displayName: String = "",
    var email: String = ""
)