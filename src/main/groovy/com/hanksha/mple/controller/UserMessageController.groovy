package com.hanksha.mple.controller

import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

import java.security.Principal

@Controller
class UserMessageController {

    static Logger logger = LoggerFactory.getLogger(UserMessageController)

    @Autowired
    SimpMessagingTemplate messaging

    @SubscribeMapping('/users')
    @SendTo('/topic/users')
    String subscribe(Principal principal) {

        logger.info(principal.name + ' subscribed')


        JsonOutput.toJson('OK')
    }

    @MessageMapping('/users')
    void test(String user, Principal principal) {
        if(principal)
            logger.info(principal.name)
        messaging.convertAndSendToUser(principal.name, '/queue/users', JsonOutput.toJson('hello everyone'))

        logger.info(user)
    }

}
