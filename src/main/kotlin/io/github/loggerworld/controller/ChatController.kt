package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.service.ChatService
import io.github.loggerworld.util.LogAware
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class ChatController(
    private val service: ChatService
) : LogAware {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    fun send(principal: Principal, request: ChatMessageRequest) =
        service.send(request)

}