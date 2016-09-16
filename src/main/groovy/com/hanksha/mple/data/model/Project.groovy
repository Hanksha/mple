package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.Canonical

@Canonical
class Project {

    int id
    String name
    String owner
    @JsonFormat(pattern = 'dd-MM-yyyy')
    Date dateCreated
    List<Commit> commits

}
