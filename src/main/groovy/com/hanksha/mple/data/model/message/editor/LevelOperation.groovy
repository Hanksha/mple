package com.hanksha.mple.data.model.message.editor

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hanksha.mple.data.model.Level

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = 'type')
@JsonSubTypes([
        @Type(value = TileOperation, name = 'TileOperation'),
        @Type(value = InsertLayerOperation, name = 'InsertLayerOperation'),
        @Type(value = MoveLayerOperation, name = 'MoveLayerOperation'),
        @Type(value = DeleteLayerOperation, name = 'DeleteLayerOperation'),
        @Type(value = AddAnnotationOperation, name = 'AddAnnotationOperation'),
        @Type(value = RemoveAnnotationOperation, name = 'RemoveAnnotationOperation'),
        @Type(value = MoveAnnotationOperation, name = 'MoveAnnotationOperation'),
        @Type(value = DrawSketchOperation, name = 'DrawSketchOperation'),
        @Type(value = RemoveSketchOperation, name = 'RemoveSketchOperation')
])
interface LevelOperation {

    boolean modify(Level level)

}