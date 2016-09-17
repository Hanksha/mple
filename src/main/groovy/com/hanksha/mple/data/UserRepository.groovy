package com.hanksha.mple.data

import com.hanksha.mple.data.model.User

interface UserRepository {

    User findOne(String name)

    List<User> findAll()

    void save(User user)

    void delete(String name)

}