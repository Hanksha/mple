function TileMap(width, height, tileWidth, tileHeight, layers) {
    this.width = width;
    this.height = height;
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
    this.layers = layers;
}

TileMap.prototype.getTileId = function (layerIndex, row, col) {
    return this.layers[layerIndex].grid[row][col];
};

TileMap.prototype.setTileId = function (layerIndex, tileIds, row, col) {
    var startRow = row - Math.floor(tileIds.length / 2);
    var startCol = col - Math.floor(tileIds[0].length / 2);

    var mapRow, mapCol;

    for(var row = 0; row < tileIds.length; row++) {
        for(var col = 0; col < tileIds[0].length; col++) {
            mapRow = startRow + row;
            mapCol = startCol + col;
            if(mapRow < 0 || mapCol < 0 ||
                mapRow >= this.height || mapCol >= this.width)
                continue;

            this.layers[layerIndex].grid[mapRow][mapCol] = tileIds[row][col];
        }
    }
};