package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Layer
import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.TileMap
import groovy.transform.Canonical

@Canonical
class InsertLayerOperation implements LevelOperation {

    int index
    String name

    boolean modify(Level level) {
        TileMap tileMap = level.tileMap

        List<Layer> layers = tileMap.layers

        if(index < 0  || index >= layers.size())
            return false

        Layer layer = new Layer(name, tileMap.height, tileMap.width)

        layers.add(index, layer)

        return true
    }

}
