package io.github.loggerworld.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.RequestUpgradeStrategy
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy
import org.springframework.web.socket.server.support.DefaultHandshakeHandler


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.run {
            enableSimpleBroker("/topic")
            setApplicationDestinationPrefixes("/app")
        }
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        val upgradeStrategy: RequestUpgradeStrategy = TomcatRequestUpgradeStrategy()
        registry.addEndpoint("/chat")
            .withSockJS()

        registry
            .addEndpoint("/chat")
            .setHandshakeHandler(DefaultHandshakeHandler(upgradeStrategy))
            .setAllowedOrigins("*")
    }
}