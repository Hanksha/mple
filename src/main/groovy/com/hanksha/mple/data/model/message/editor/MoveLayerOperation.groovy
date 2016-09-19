package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Layer
import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class MoveLayerOperation implements LevelOperation {

    int dir
    int index

    void modify(Level level) {
        List<Layer> layers = level.tileMap.layers

        Layer layer = layers[index]
        layers[index] = layers[index + dir]
        layers[index + dir] = layer
    }
}
