package com.hanksha.mple.exception

class TilesetAlreadyExistsException extends Exception {

    TilesetAlreadyExistsException(String name) {
        super("A tileset with name '$name' already exists")
    }

}
