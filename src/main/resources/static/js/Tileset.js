function Tileset(offsetX, offsetY, tileWidth, tileHeight, numRow, numCol, spacing) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
    this.numRow = numRow;
    this.numCol = numCol;
    this.spacing = spacing;
}

Tileset.prototype.getId = function (row, col) {
    return col + 1 + row * this.numCol;
};