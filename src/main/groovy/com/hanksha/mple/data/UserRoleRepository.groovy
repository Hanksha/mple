package com.hanksha.mple.data

interface UserRoleRepository {

    List<String> findRoles(String username)

}
