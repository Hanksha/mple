package com.hanksha.mple.data

import com.hanksha.mple.data.model.Tileset

interface TilesetRepository {

    List<Tileset> findAll()

    Tileset findOne(int id)

    Tileset findOne(String name)

    void save(Tileset tileset)

    void update(Tileset tileset)

    void delete(int id)

}
