package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level

class RemoveAnnotationOperation implements LevelOperation {

    int index

    boolean modify(Level level) {
        if(index < 0 || index >= level.annotations.size())
            return false

        level.annotations.remove(index);

        return true
    }
}
