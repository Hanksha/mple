function TilesetRenderer(renderer, tileset) {

    this.renderer = renderer;
    this.tileset = tileset;
    this.container = new PIXI.Container();

}

TilesetRenderer.prototype.refresh = function (offsetX, offsetY, scale) {
    this.container.removeChildren(0, this.container.children.length);

    var startRow = offsetY < 0? Math.floor(Math.abs(offsetY / (this.tileset.tileHeight * scale))) : 0;
    var startCol = offsetX < 0? Math.floor(Math.abs(offsetX / (this.tileset.tileWidth * scale))) : 0;
    var endRow = Math.min(this.tileset.numRow, Math.floor(Math.abs((this.renderer.height - offsetY) / (this.tileset.tileHeight * scale))));
    var endCol = Math.min(this.tileset.numCol, Math.floor(Math.abs(this.renderer.width - offsetX) / (this.tileset.tileWidth * scale)));

    for(var row = startRow; row < endRow; row++) {
        for(var col = startCol; col < endCol; col++) {
            var id = (col + 1) +  row * this.tileset.numCol;
            var tile = PIXI.Sprite.fromFrame(id.toString());
            tile.position.x = col * this.tileset.tileWidth;
            tile.position.y = row * this.tileset.tileHeight;
            this.container.addChild(tile);
        }
    }
};