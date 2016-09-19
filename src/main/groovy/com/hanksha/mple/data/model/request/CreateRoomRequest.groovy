package com.hanksha.mple.data.model.request

import groovy.transform.Canonical

import javax.validation.constraints.NotNull

@Canonical
class CreateRoomRequest {

    @NotNull
    String name
    @NotNull
    String projectName
    @NotNull
    String levelName

    String levelVersion
}
