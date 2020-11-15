package io.github.loggerworld.service

import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.dto.response.ChatMessageResponse
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatService {

    fun send(request: ChatMessageRequest) =
        ChatMessageResponse(request.from, request.message, LocalDateTime.now())
}