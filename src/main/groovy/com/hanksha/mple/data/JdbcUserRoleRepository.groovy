package com.hanksha.mple.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

import java.sql.ResultSet
import java.sql.SQLException

@Repository
class JdbcUserRoleRepository implements UserRoleRepository {

    @Autowired
    JdbcOperations jdbc

    List<String> findRoles(String username) {
        jdbc.query('SELECT role from user_roles WHERE username = ?', new UserRoleRowMapper(), username)
    }

    void save(String username, String role) {
        jdbc.update('INSERT INTO user_roles VALUES (default, ?, ?)', username, role)
    }

    private class UserRoleRowMapper implements RowMapper<String> {
        String mapRow(ResultSet rs, int rowNum) throws SQLException {
            rs.getString('role')
        }
    }

}
