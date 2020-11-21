package io.github.loggerworld.dto.response

import io.github.loggerworld.domain.enums.Languages

data class UserResponse(
    var id: Long = -1,
    var loginName: String = "",
    var language: Languages = Languages.EN,
    var displayName: String = "",
    var email: String = ""
)