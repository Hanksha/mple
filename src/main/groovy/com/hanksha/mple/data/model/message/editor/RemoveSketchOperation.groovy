package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class RemoveSketchOperation implements LevelOperation {

    int index

    boolean modify(Level level) {
        if(index < 0 || index >= level.sketches.size())
            return false

        level.sketches.remove(index)

        return true
    }

}
