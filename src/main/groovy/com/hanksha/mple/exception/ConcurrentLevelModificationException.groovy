package com.hanksha.mple.exception

class ConcurrentLevelModificationException extends Exception {

    ConcurrentLevelModificationException(String projectName, String levelName, String roomName) {
        super("Level '$levelName' in $projectName is already currently being edited in room '$roomName'")
    }

}
