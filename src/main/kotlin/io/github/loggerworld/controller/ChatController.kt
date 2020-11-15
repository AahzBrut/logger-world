package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.service.ChatService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val service: ChatService
) {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    fun send(request: ChatMessageRequest) =
        service.send(request)
}