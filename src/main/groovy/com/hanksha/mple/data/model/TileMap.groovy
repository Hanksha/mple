package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class TileMap {

    @JsonProperty
    int width
    @JsonProperty
    int height
    @JsonProperty
    int tileWidth
    @JsonProperty
    int tileHeight
    @JsonProperty
    ArrayList<Layer> layers

    TileMap() {}

    TileMap(int width, int height, int tileWidth, int tileHeight) {
        layers = []
        this.width = width
        this.height = height
        this.tileWidth = tileWidth
        this.tileHeight = tileHeight
        layers.add(new Layer('Layer 1', width, height))
    }

}
