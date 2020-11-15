package io.github.loggerworld.dto.response

import java.time.LocalDateTime

data class ChatMessageResponse(
    var from: String,
    var message: String,
    var time: LocalDateTime
)