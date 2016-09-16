package com.hanksha.mple.exception

class LevelNotFoundException extends Exception {

    LevelNotFoundException(String projectName, String levelName) {
        super("Could not find level '$levelName' in project '$projectName'")
    }

    LevelNotFoundException(int id) {
        super("Could not find level with ID $id")
    }

}
