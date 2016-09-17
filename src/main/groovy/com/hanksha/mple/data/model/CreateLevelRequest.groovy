package com.hanksha.mple.data.model

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class CreateLevelRequest {

    @NotNull
    String name

    @NotNull
    String tileset

    @NotNull
    @Min(1l)
    int width

    @NotNull
    @Min(1l)
    int height

    @NotNull
    @Min(1l)
    int tileWidth

    @NotNull
    @Min(1l)
    int tileHeight

}
