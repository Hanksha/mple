package com.hanksha.mple.exception

class ForbiddenRoomAccessException extends Exception {

    ForbiddenRoomAccessException(int id) {
        super("Cannot access room ID$id, must connect to the room first.")
    }

}
