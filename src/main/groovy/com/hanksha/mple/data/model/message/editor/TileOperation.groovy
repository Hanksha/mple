package com.hanksha.mple.data.model.message.editor

import com.hanksha.mple.data.model.Level
import groovy.transform.Canonical

@Canonical
class TileOperation implements LevelOperation {

    int layerIndex
    int startRow
    int startCol
    int[][] tiles

    boolean modify(Level level) {
        int[][] grid = level.tileMap.layers[layerIndex]?.grid

        if(!grid ||
            startRow < 0 || startRow >= grid.length ||
            startCol < 0 || startCol >= grid[0].length)
            return false

        int mapRow, mapCol

        for(int row = 0; row < tiles.length; row++) {
            for(int col = 0; col < tiles[0].length; col++) {
                mapRow = startRow + row
                mapCol = startCol + col

                if(mapRow < 0 || mapCol < 0 || mapRow >= grid.length || mapCol >= grid[0].length)
                    continue;

                grid[mapRow][mapCol] = tiles[row][col]
            }
        }

        return true;
    }
}
