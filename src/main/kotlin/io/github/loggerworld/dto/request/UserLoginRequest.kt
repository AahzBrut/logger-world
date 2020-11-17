package io.github.loggerworld.dto.request

data class UserLoginRequest(
    var userName: String = "",
    var password: String = ""
)