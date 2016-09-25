package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class MoveAnnotationOperation implements LevelOperation {

    int index
    int x
    int y

    boolean modify(Level level) {

        if(index < 0 || index >= level.annotations.size())
            return false

        level.annotations[index].x = x
        level.annotations[index].y = y

        return true
    }

}
