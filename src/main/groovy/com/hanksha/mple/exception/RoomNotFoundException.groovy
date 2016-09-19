package com.hanksha.mple.exception

class RoomNotFoundException extends Exception {

    RoomNotFoundException(long id) {
        super("Could not find room with id '$id'")
    }

}
