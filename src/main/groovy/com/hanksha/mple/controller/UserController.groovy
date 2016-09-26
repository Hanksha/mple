package com.hanksha.mple.controller

import com.hanksha.mple.data.UserRepository
import com.hanksha.mple.data.UserRoleRepository
import com.hanksha.mple.data.model.User
import com.hanksha.mple.data.model.request.ChangePasswordRequest
import com.hanksha.mple.data.model.request.CreateUserRequest
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.annotation.PostConstruct
import java.security.Principal

@RestController
@RequestMapping('/api/users')
class UserController {

    @Autowired
    UserRepository userRepo

    @Autowired
    UserRoleRepository userRoleRepo

    @PostConstruct
    void init() {
        if(!userRepo.findOne('admin')) {
            userRepo.save(new User(name: 'admin', password: 'Aseft1357', enabled: true))
            userRoleRepo.save('admin', 'ROLE_ADMIN')
            userRoleRepo.save('admin', 'ROLE_USER')
        }
    }

    @PostMapping('/{username}')
    ResponseEntity changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request, Principal user) {

        println user.class

        if(user.name != username)
            return new ResponseEntity(JsonOutput.toJson('Cannot change the password of someone else!'), HttpStatus.FORBIDDEN)

        if(request.newPassword != request.newPasswordConfirmed)
            return new ResponseEntity(JsonOutput.toJson('Passwords do not match'), HttpStatus.BAD_REQUEST)

        userRepo.update(new User(name: username, password: request.newPassword, enabled: true))
    }

    @GetMapping('')
    ResponseEntity getUsers() {

        List<String> usernames = userRepo.findAll().collect {it.name}

        new ResponseEntity(usernames, HttpStatus.OK)
    }

    @PostMapping('')
    ResponseEntity saveUser(@RequestBody CreateUserRequest request) {
        try {
            userRepo.save(new User(name: request.username, password: request.password, enabled: true))

            if(request.role == 'user') {
                userRoleRepo.save(request.username, 'ROLE_USER')
            }
            else if(request.role == 'admin') {
                userRoleRepo.save(request.username, 'ROLE_USER')
                userRoleRepo.save(request.username, 'ROLE_ADMIN')
            }

        } catch(Exception ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity.ok(JsonOutput.toJson('User created'))
    }

    @DeleteMapping('/{username}')
    ResponseEntity deleteUser(@PathVariable String username) {
        try {
            userRepo.delete(username)
        } catch(Exception ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }

        return ResponseEntity.ok(JsonOutput.toJson('User deleted'))
    }
}
