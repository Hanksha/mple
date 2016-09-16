package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

import java.awt.Point

@ToString
class Level {

    @JsonProperty
    String name
    @JsonProperty
    String tileset
    @JsonProperty
    TileMap tileMap
    @JsonProperty
    LevelObject[] objects
    @JsonProperty
    Annotation[] annotations
    @JsonProperty
    SketchLine[] sketches

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

    class Annotation {

        @JsonProperty
        String text
        @JsonProperty
        String color
        @JsonProperty
        Point position

    }

    class SketchLine {

        @JsonProperty
        String color
        @JsonProperty
        Point tail
        @JsonProperty
        Point head

    }

}
