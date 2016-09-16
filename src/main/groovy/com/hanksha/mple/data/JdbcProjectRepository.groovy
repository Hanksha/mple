package com.hanksha.mple.data

import com.hanksha.mple.data.model.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

import java.sql.ResultSet
import java.sql.SQLException

@Repository
class JdbcProjectRepository implements ProjectRepository {

    @Autowired
    JdbcOperations jdbc

    Project findOne(String name) {
        Project project = null

        try {
            project = jdbc.queryForObject('SELECT id, name, date_created, owner FROM projects where name = ?', new ProjectMapper(), name)
        } catch(EmptyResultDataAccessException ex) {}

        project
    }

    Project findOne(int id) {
        Project project = null

        try {
            project = jdbc.queryForObject('SELECT id, name, date_created, owner FROM projects where id = ?', new ProjectMapper(), id)
        } catch(EmptyResultDataAccessException ex) {}

        project
    }

    List<Project> findAll() {
        jdbc.query('SELECT id, name, date_created, owner FROM projects', new ProjectMapper())
    }

    void save(Project project) {
        jdbc.update('INSERT INTO projects VALUES (DEFAULT ,?,?,?)', project.name, new java.sql.Date(project.dateCreated.getTime()), project.owner)
    }

    void update(Project project) {
        jdbc.update('UPDATE projects SET name = ?, owner = ? WHERE id = ?', project.name, project.owner, project.id)
    }

    void delete(String name) {
        jdbc.update('DELETE FROM projects WHERE name = ?', name)
    }

    private class ProjectMapper implements RowMapper<Project> {
        Project mapRow(ResultSet rs, int rowNum) throws SQLException {
            new Project(
                    id: rs.getInt('id'),
                    name: rs.getString('name'),
                    dateCreated: new Date(rs.getDate('date_created').getTime()),
                    owner: rs.getString('owner')
            )
        }
    }
}
