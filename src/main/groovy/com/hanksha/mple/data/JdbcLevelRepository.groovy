package com.hanksha.mple.data

import com.hanksha.mple.data.model.LevelMeta
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

import java.sql.ResultSet
import java.sql.SQLException

@Repository
class JdbcLevelRepository implements LevelRepository {

    @Autowired
    JdbcOperations jdbc

    LevelMeta findOne(int id) {
        LevelMeta levelMeta = null

        try {
            levelMeta = jdbc.queryForObject('SELECT project_id, id, date_created, name FROM levels WHERE id = ?', new LevelMetaRowMapper(), id)
        } catch (EmptyResultDataAccessException ex) {}

        levelMeta
    }

    LevelMeta findOne(int projectId, String name) {
        LevelMeta levelMeta = null

        try {
            levelMeta = jdbc.queryForObject('SELECT project_id, id, date_created, name FROM levels WHERE project_id = ? AND name = ?',
                    new LevelMetaRowMapper(), projectId, name)
        } catch (EmptyResultDataAccessException ex) {}

        levelMeta
    }

    List<LevelMeta> findAll() {
        jdbc.query('SELECT project_id, id, date_created, name FROM levels', new LevelMetaRowMapper())
    }

    void save(LevelMeta levelMeta) {
        jdbc.update('INSERT INTO levels VALUES (?,default,?,?)',
                    levelMeta.projectId,
                    levelMeta.name,
                    new java.sql.Date(levelMeta.dateCreated.getTime()))
    }

    void update(LevelMeta levelMeta) {
        jdbc.update('UPDATE levels SET name = ? WHERE id = ?', levelMeta.name, levelMeta.id)
    }

    void delete(int id) {
        jdbc.update('DELETE FROM levels WHERE id = ?', id)
    }

    private class LevelMetaRowMapper implements RowMapper<LevelMeta> {

        LevelMeta mapRow(ResultSet rs, int i) throws SQLException {
            new LevelMeta(
                    projectId: rs.getInt('project_id'),
                    id: rs.getInt('id'),
                    name: rs.getString('name'),
                    dateCreated: new Date(rs.getDate('date_created').getTime()))
        }
    }
}
