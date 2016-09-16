package com.hanksha.mple.exception

/**
 * Created by vivien on 9/13/16.
 */
class ProjectNotFoundException extends Exception {

    ProjectNotFoundException(String name) {
        super("Could not find project with name '$name'")
    }

}
