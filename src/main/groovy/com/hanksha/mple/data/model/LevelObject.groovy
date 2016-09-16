package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class LevelObject {

    @JsonProperty
    String name
    @JsonProperty
    Map properties
    @JsonProperty
    LevelObject[] childs

}
