package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.SketchLine
import groovy.transform.Canonical

import java.awt.*

@Canonical
class DrawSketchOperation implements LevelOperation {

    int x1, x2, y1, y2

    boolean modify(Level level) {
        SketchLine line = new SketchLine('red', new Point(x1, y1), new Point(x2, y2))
        level.sketches << line

        return true
    }

}
