package io.github.loggerworld.dto.response

data class ResponseObject(
    var success: Boolean = true,
    var message: String = "",
    var data: Any? = null
)
