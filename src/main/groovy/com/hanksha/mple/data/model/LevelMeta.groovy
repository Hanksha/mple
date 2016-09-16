package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.Canonical

@Canonical
class LevelMeta {

    int projectId
    int id
    String name
    @JsonFormat(pattern = 'dd-MM-yyyy')
    Date dateCreated

}
