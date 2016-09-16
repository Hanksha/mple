package com.hanksha.mple.data.model

import groovy.transform.Canonical

/**
 * Created by vivien on 8/28/16.
 */

@Canonical
class Tileset {

    int id
    String name
    Date dateCreated
    int spacing
    int offsetX
    int offsetY
    int tileWidth
    int tileHeight
    int numRow
    int numCol
    String fileName

}
