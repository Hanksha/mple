package com.hanksha.mple.exception

/**
 * Created by vivien on 9/13/16.
 */
class LevelAlreadyExistsException extends Exception {

    LevelAlreadyExistsException(String projectName, String levelName) {
        super("A level with name '$levelName' already exists in project '$projectName'")
    }

}
