package io.github.loggerworld.service

import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.dto.response.chat.ChatMessageResponse
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ChatService {

    fun send(request: ChatMessageRequest) =
        ChatMessageResponse(request.from, request.message, OffsetDateTime.now())
}