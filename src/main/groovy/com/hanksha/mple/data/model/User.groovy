package com.hanksha.mple.data.model

import groovy.transform.Canonical

@Canonical
class User {

    String name
    String password
    boolean enabled

}
