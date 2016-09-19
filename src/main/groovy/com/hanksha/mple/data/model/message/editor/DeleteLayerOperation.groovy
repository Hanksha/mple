package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class DeleteLayerOperation implements LevelOperation {

    int index

    void modify(Level level) {
        level.tileMap.layers.remove(index)
    }

}
