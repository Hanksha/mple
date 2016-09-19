package com.hanksha.mple.service.auth

import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

interface UserService extends AuthenticationSuccessHandler, LogoutSuccessHandler {

    List<User> getAllUsers()

}