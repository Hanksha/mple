package com.hanksha.mple.data.model

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.Canonical

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Canonical
class Tileset {

    int id
    @NotNull
    String name
    @JsonFormat(pattern = 'dd-MM-yyyy')
    Date dateCreated
    @NotNull
    @Min(0l)
    int spacing
    @NotNull
    @Min(0l)
    int offsetX
    @NotNull
    @Min(0l)
    int offsetY
    @NotNull
    @Min(1l)
    int tileWidth
    @NotNull
    @Min(1l)
    int tileHeight
    @NotNull
    @Min(1l)
    int numRow
    @NotNull
    @Min(1l)
    int numCol
    @NotNull
    String imgSrc
}
