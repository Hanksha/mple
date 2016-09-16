function TileMapRenderer(renderer, tileMap) {

    this.renderer = renderer;
    this.tileMap = tileMap;
    this.container = new PIXI.Container();

}

TileMapRenderer.prototype.refresh = function (viewPort) {
    this.container.removeChildren(0, this.container.children.length);

    var startRow = viewPort.y < 0? Math.floor(Math.abs(viewPort.y / (this.tileMap.tileHeight * viewPort.scale))) : 0;
    var startCol = viewPort.x < 0? Math.floor(Math.abs(viewPort.x / (this.tileMap.tileWidth * viewPort.scale))) : 0;
    var endRow = Math.min(this.tileMap.height, Math.floor(Math.abs((this.renderer.height - viewPort.y) / (this.tileMap.tileHeight * viewPort.scale))));
    var endCol = Math.min(this.tileMap.width, Math.floor(Math.abs(this.renderer.width - viewPort.x) / (this.tileMap.tileWidth * viewPort.scale)));

    for(var index = 0; index < this.tileMap.layers.length; index++) {
        var layer = this.tileMap.layers[index];

        if(!layer.visible)
            continue;

        for(var row = startRow; row < endRow; row++) {
            for(var col = startCol; col < endCol; col++) {
                if(layer.grid[row][col] == 0)
                    continue;
                var tile = PIXI.Sprite.fromFrame(layer.grid[row][col].toString());
                tile.position.x = col * this.tileMap.tileWidth;
                tile.position.y = row * this.tileMap.tileHeight;
                this.container.addChild(tile);
            }
        }
    }
};