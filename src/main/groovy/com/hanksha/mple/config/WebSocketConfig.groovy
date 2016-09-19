package com.hanksha.mple.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker('/topic', '/queue')
        registry.setApplicationDestinationPrefixes('/app')
    }

    void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint('/mple').withSockJS()
    }
}
