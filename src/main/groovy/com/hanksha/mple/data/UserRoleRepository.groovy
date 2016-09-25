package com.hanksha.mple.data

interface UserRoleRepository {

    List<String> findRoles(String username)

    void save(String username, String role)
}
