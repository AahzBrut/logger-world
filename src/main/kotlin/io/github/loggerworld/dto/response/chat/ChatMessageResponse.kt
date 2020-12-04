package io.github.loggerworld.dto.response.chat

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ChatMessageResponse(
    var from: String = "",
    var message: String = "",
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var time: LocalDateTime = LocalDateTime.now()
)