package com.hanksha.mple.exception

class TilesetNotFoundException extends Exception {

    TilesetNotFoundException(String name) {
        super("Could not find tileset with name '$name'")
    }

}
