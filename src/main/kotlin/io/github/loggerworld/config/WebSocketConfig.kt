package io.github.loggerworld.config

import io.github.loggerworld.service.security.JwtService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_CONNECTION_POINT
import io.github.loggerworld.util.WS_DESTINATION_PREFIX
import io.github.loggerworld.util.WS_GAMEPLAY_COMBAT_EVENT_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_LOCATION_NOTIFICATION_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_LOG_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_WRONG_COMMAND_QUEUE
import io.github.loggerworld.util.WS_TOPIC
import io.github.loggerworld.util.logger
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.util.MultiValueMap
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.RequestUpgradeStrategy
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy
import org.springframework.web.socket.server.support.DefaultHandshakeHandler


const val NATIVE_HEADERS = "nativeHeaders"
const val AUTH_FAILED_MESSAGE = "JWT token not provided."

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketConfig(
    private val jwtService: JwtService
) : WebSocketMessageBrokerConfigurer, LogAware {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.run {
            enableSimpleBroker(
                WS_TOPIC,
                WS_GAMEPLAY_LOCATION_NOTIFICATION_QUEUE,
                WS_GAMEPLAY_WRONG_COMMAND_QUEUE,
                WS_GAMEPLAY_LOG_QUEUE,
                WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE,
                WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE,
                WS_GAMEPLAY_COMBAT_EVENT_QUEUE,
            )
            setApplicationDestinationPrefixes(WS_DESTINATION_PREFIX)
        }
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        val upgradeStrategy: RequestUpgradeStrategy = TomcatRequestUpgradeStrategy()
        registry.addEndpoint(WS_CONNECTION_POINT)
            .withSockJS()

        registry
            .addEndpoint(WS_CONNECTION_POINT)
            .setHandshakeHandler(DefaultHandshakeHandler(upgradeStrategy))
            .setAllowedOrigins("*")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {

            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!
                if (StompCommand.CONNECT != accessor.command) return message

                val authToken = (accessor.messageHeaders[NATIVE_HEADERS] as MultiValueMap<*, *>)[HttpHeaders.AUTHORIZATION]?.get(0) as String?
                if (authToken == null || !jwtService.validateToken(authToken)) throw AuthenticationCredentialsNotFoundException(AUTH_FAILED_MESSAGE)

                val userName = jwtService.extractUsername(authToken)
                val authUser = UsernamePasswordAuthenticationToken(userName, null, emptyList())
                accessor.user = authUser
                logger().info("STOMP пользователь: ${authUser.name}")
                return message
            }
        })
    }
}