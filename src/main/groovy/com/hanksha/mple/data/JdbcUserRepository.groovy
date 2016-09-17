package com.hanksha.mple.data

import com.hanksha.mple.data.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

import java.sql.ResultSet
import java.sql.SQLException

@Repository
class JdbcUserRepository implements UserRepository {

    @Autowired
    JdbcOperations jdbc

    User findOne(String name) {
        User user = null

        try {
            user = jdbc.queryForObject('SELECT username, password, enabled FROM users WHERE username = ?', new UserRowMapper(), name)
        } catch(EmptyResultDataAccessException ex) {}

        user
    }

    List<User> findAll() {
        jdbc.query('SELECT username, password, enabled FROM users', new UserRowMapper())
    }

    void save(User user) {
        jdbc.update('INSERT INTO users VALUES (?,?,?)', user.name, user.password, user.enabled)
    }

    void update(User user) {
        jdbc.update('UPDATE users SET password = ?, enabled = ? WHERE username = ?', user.password, user.enabled?1:0, user.name)
    }

    void delete(String name) {
        jdbc.update('DELETE FROM users WHERE username = ?', name)
    }

    private class UserRowMapper implements RowMapper<User> {
        User mapRow(ResultSet rs, int rowNum) throws SQLException {
            new User(
                    name: rs.getString('username'),
                    password: rs.getString('password'),
                    enabled: rs.getInt('enabled') == 1)
        }
    }
}
