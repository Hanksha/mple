package com.hanksha.mple.config

import com.hanksha.mple.handler.MarcoHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Created by vivien on 8/23/16.
 */

@Configuration
@EnableWebSocket
class WebSocketConfig implements WebSocketConfigurer {

    @Override
    void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(marcoHandler(), '/marco').withSockJS()
    }

    @Bean
    MarcoHandler marcoHandler() {
        new MarcoHandler()
    }
}
