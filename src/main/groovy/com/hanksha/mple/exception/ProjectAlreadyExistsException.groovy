package com.hanksha.mple.exception

/**
 * Created by vivien on 9/12/16.
 */
class ProjectAlreadyExistsException extends Exception {

    ProjectAlreadyExistsException(String name) {
        super("A project named '$name' already exists")
    }

}
