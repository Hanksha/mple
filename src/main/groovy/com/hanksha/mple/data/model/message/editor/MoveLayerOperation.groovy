package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Layer
import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class MoveLayerOperation implements LevelOperation {

    int dir
    int index

    boolean modify(Level level) {
        List<Layer> layers = level.tileMap.layers

        if(index + dir < 0 || index + dir >= layers.size())
            return false

        Layer layer = layers[index]
        layers[index] = layers[index + dir]
        layers[index + dir] = layer

        return true
    }
}
