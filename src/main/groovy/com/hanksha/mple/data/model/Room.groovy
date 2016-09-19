package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

@Canonical
class Room {
    int id
    String name
    String projectName
    List<String> users

    @JsonIgnore
    Level level

    @JsonProperty
    String getLevelName() {
        level.name
    }
}
