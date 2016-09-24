package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class DeleteLayerOperation implements LevelOperation {

    int index

    boolean modify(Level level) {
        if(index < 0  || index >= level.tileMap.layers.size())
            return false

        level.tileMap.layers.remove(index)

        return true
    }

}
