package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class Level {

    @JsonProperty
    String name
    @JsonProperty
    String tileset
    @JsonProperty
    TileMap tileMap
    @JsonProperty
    List<LevelObject> objects
    @JsonProperty
    List<Annotation> annotations
    @JsonProperty
    List<SketchLine> sketches

    Level() {}

    Level(String name, String tileset,
          int width, int height,
          int tileWidth, int tileHeight) {
        this.name = name;
        this.tileset = tileset
        tileMap = new TileMap(width, height, tileWidth, tileHeight)
        objects = []
        annotations = []
        sketches = []
    }

}
