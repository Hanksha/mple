package com.hanksha.mple.data

import com.hanksha.mple.data.model.Tileset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

import java.sql.ResultSet
import java.sql.SQLException

/**
 * Created by vivien on 8/28/16.
 */

@Repository
class JdbcTilesetRepository implements TilesetRepository {

    @Autowired
    JdbcOperations jdbc

    List<Tileset> findAll() {
        jdbc.query('SELECT id, name, date_created, spacing, offset_x, offset_y, ' +
                    'tile_width, tile_height, num_row, num_col FROM tilesets', new TilesetRowMapper())
    }

    Tileset findOne(int id) {
        try {
            jdbc.queryForObject('SELECT id, name, date_created, spacing, offset_x, offset_y, ' +
                    'tile_width, tile_height, num_row, num_col FROM tilesets WHERE id = ?',
                    new TilesetRowMapper(), id)
        } catch(EmptyResultDataAccessException e) { }
    }

    Tileset findOne(String name) {
        try {
            jdbc.queryForObject('SELECT id, name, date_created, spacing, offset_x, offset_y, ' +
                    'tile_width, tile_height, num_row, num_col FROM tilesets WHERE name = ?',
                    new TilesetRowMapper(), name)
        } catch(EmptyResultDataAccessException e) { }
    }

    void save(Tileset tileset) {
        jdbc.update('INSERT INTO tilesets VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                tileset.name,
                new java.sql.Date(tileset.dateCreated.getTime()),
                tileset.spacing,
                tileset.offsetX,
                tileset.offsetY,
                tileset.tileWidth,
                tileset.tileHeight,
                tileset.numRow,
                tileset.numCol
            )
    }

    void update(Tileset tileset) {
        jdbc.update('UPDATE tilesets ' +
                    'SET name = ?, spacing = ?, offset_x = ?, offset_y = ?, ' +
                    'tile_width = ?, tile_height = ?, num_row = ?, num_col = ? WHERE id = ?',
                tileset.name,
                tileset.spacing,
                tileset.offsetX,
                tileset.offsetY,
                tileset.tileWidth,
                tileset.tileHeight,
                tileset.numRow,
                tileset.numCol,
                tileset.id)
    }

    void delete(int id) {
        jdbc.update('DELETE FROM tilesets WHERE id = ?', id)
    }

    private class TilesetRowMapper implements RowMapper<Tileset> {

        Tileset mapRow(ResultSet rs, int i) throws SQLException {
            new Tileset(
                    id: rs.getInt('id'),
                    name: rs.getString('name'),
                    dateCreated: new Date(rs.getDate('date_created').getTime()),
                    spacing: rs.getInt('spacing'),
                    offsetX: rs.getInt('offset_x'),
                    offsetY: rs.getInt('offset_y'),
                    tileWidth: rs.getInt('tile_width'),
                    tileHeight: rs.getInt('tile_height'),
                    numRow: rs.getInt('num_row'),
                    numCol: rs.getInt('num_col')
            )
        }
    }
}
