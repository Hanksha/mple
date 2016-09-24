package com.hanksha.mple.controller

import com.hanksha.mple.data.UserRepository
import com.hanksha.mple.data.model.User
import com.hanksha.mple.data.model.request.ChangePasswordRequest
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

@RestController
@RequestMapping('/api/users')
class UserController {

    @Autowired
    UserRepository userRepo

    @PostMapping('/{username}')
    ResponseEntity changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request, Principal user) {

        println user.class

        if(user.name != username)
            return new ResponseEntity(JsonOutput.toJson('Cannot change the password of someone else!'), HttpStatus.FORBIDDEN)

        if(request.newPassword != request.newPasswordConfirmed)
            return new ResponseEntity(JsonOutput.toJson('Passwords do not match'), HttpStatus.BAD_REQUEST)

        userRepo.update(new User(name: username, password: request.newPassword, enabled: true))
    }

}
