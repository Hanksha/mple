package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

import java.awt.Point

@Canonical
class SketchLine {

    @JsonProperty
    String color
    @JsonProperty
    Point tail
    @JsonProperty
    Point head

}
