package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.Canonical

@Canonical
public class Commit {

    String name
    String author
    String message
    @JsonFormat(pattern = 'dd-MM-yyyy HH:mm a')
    Date time
    List<String> changedFileNames

}