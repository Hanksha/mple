package com.hanksha.mple.service.auth.impl

import com.hanksha.mple.data.UserRepository
import com.hanksha.mple.data.model.message.AlertMessage
import com.hanksha.mple.service.auth.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepo

    @Autowired
    SimpMessagingTemplate messaging

    @Autowired
    SessionRegistry sessionRegistry

    List<User> getAllUsers() {
        sessionRegistry.allPrincipals.findAll{
            User user = (User) it
            !sessionRegistry.getAllSessions(user, false).isEmpty()
        } collect {
            User user = (User) it
            user
        }
    }

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        AlertMessage alertMessage = new AlertMessage(message: "${authentication.name} just logged in", type: 'info')
        messaging.convertAndSend('/topic/alerts', alertMessage)

        response.addCookie(new Cookie('username', authentication.name))
        response.addCookie(new Cookie('roles', authentication.getAuthorities().collect {it.authority}.join('.')))
        response.setStatus(HttpServletResponse.SC_OK)
    }

    void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String username = authentication?authentication.name:'anonymous'
        AlertMessage alertMessage = new AlertMessage(message: "$username just logged out", type: 'info')
        messaging.convertAndSend('/topic/alerts', alertMessage)
        response.setStatus(HttpServletResponse.SC_OK)
    }
}
