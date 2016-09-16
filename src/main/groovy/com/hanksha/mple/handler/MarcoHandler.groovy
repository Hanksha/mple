package com.hanksha.mple.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.AbstractWebSocketHandler

/**
 * Created by vivien on 8/23/16.
 */
class MarcoHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(MarcoHandler)

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        logger.info("Received message: ${message.getPayload()}")

        Thread.sleep(2000)

        session.sendMessage(new TextMessage('I love you too, Medita!'))
    }
}
