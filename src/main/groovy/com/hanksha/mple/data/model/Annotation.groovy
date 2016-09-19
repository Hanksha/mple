package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

@Canonical
class Annotation {

    @JsonProperty
    String text
    @JsonProperty
    String color
    @JsonProperty
    int x
    @JsonProperty
    int y

}
