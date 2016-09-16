package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class Layer {

    @JsonProperty
    int[][] grid
    @JsonProperty
    String name

    Layer() {}

    Layer(String name, int numRow, int numCol) {
        grid = new int[numRow][numCol]
        this.name = name
    }

}
