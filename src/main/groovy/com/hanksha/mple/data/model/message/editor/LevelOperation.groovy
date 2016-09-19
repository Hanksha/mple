package com.hanksha.mple.data.model.message.editor

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hanksha.mple.data.model.Level

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = 'type')
@JsonSubTypes([
        @Type(value = TileOperation, name = 'tileOperation')
])
interface LevelOperation {

    void modify(Level level)

}