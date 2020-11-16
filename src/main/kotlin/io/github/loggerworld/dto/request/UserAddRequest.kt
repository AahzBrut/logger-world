package io.github.loggerworld.dto.request

data class UserAddRequest(
    var userName: String = "",
    var password: String = "",
    var language: String = "",
    var displayName: String = "",
    var email: String = ""
)