package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Annotation
import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class AddAnnotationOperation implements LevelOperation {

    int x
    int y
    String text

    boolean modify(Level level) {
        level.annotations.add(new Annotation(text: text, x: x, y: y))

        return true
    }

}
