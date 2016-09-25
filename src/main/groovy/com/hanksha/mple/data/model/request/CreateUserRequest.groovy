package com.hanksha.mple.data.model.request

import groovy.transform.Canonical

@Canonical
class CreateUserRequest {

    String username
    String password
    String role

}
