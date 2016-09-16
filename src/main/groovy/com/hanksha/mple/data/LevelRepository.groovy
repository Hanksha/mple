package com.hanksha.mple.data

import com.hanksha.mple.data.model.LevelMeta

interface LevelRepository {

    List<LevelMeta> findAll()

    LevelMeta findOne(int id)

    LevelMeta findOne(int projectId, String name)

    void save(LevelMeta levelMeta)

    void update(LevelMeta levelMeta)

    void delete(int id)

}
