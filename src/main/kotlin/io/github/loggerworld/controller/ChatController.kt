package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.service.ChatService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_CHAT
import io.github.loggerworld.util.WS_DS_TOPIC_MESSAGES
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class ChatController(
    private val service: ChatService
) : LogAware {

    @MessageMapping(WS_CHAT)
    @SendTo(WS_DS_TOPIC_MESSAGES)
    fun send(principal: Principal, request: ChatMessageRequest) =
        service.send(request)

}