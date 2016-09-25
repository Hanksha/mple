function LevelEditor($routeParams, $log, hotkeys, $location, $uibModal,
                     Tools, LevelService, RoomService, TilesetService,
                     EventService, MessagingService, AlertService) {
    var self = this;
    this.roomId = $routeParams.roomId;
    this.loaded = false;
    this.level = null;
    this.tileMap = null;
    this.objects = null;
    this.annotations = [];
    this.sketches = [];

    this.tools = Tools;
    this.messaging = MessagingService;
    this.roomService = RoomService;
    this.eventService = EventService;
    this.tilesetService = TilesetService;
    this.levelService = LevelService;
    this.uibModal = $uibModal;
    this.alertService = AlertService;
    this.location = $location;
    this.mouse = new Mouse();
    this.cursor = new Cursor();
    this.viewPort = new ViewPort();
    this.activeTool = Tools.getTool('brush');
    this.toolOptions = {};
    this.selectedTiles = [[0]];
    this.selectedLayer = 0;
    this.showGrid = true;
    this.showAnnotations = true;
    this.showSketches = true;
    this.refreshAnnotations = true;
    this.refreshTiles = true;

    this.renderer = null;
    this.stage = new PIXI.Container();
    this.annotationContainer = new PIXI.Container();
    this.selectedTilesPreview = new PIXI.Container();
    this.gridGraphics = new PIXI.Graphics();
    this.sketchGraphics = new PIXI.Graphics();
    this.cursorHover = new PIXI.Graphics();
    this.levelRenderer = null;

    // Keyboard shortcuts
    hotkeys.add({
        combo: 'shift+left',
        callback: function() {
            self.viewPort.x -= 10;
        }
    });
    hotkeys.add({
        combo: 'shift+right',
        callback: function() {
            self.viewPort.x += 10;
        }
    });
    hotkeys.add({
        combo: 'shift+up',
        callback: function() {
            self.viewPort.y -= 10;
        }
    });
    hotkeys.add({
        combo: 'shift+down',
        callback: function() {
            self.viewPort.y += 10;
        }
    });
    hotkeys.add({
        combo: 'shift+f',
        callback: function() {
            self.flipHTiles();
        }
    });
    hotkeys.add({
        combo: 'shift+v',
        callback: function() {
            self.flipVTiles();
        }
    });

    EventService.subscribe('tileset-loaded', function (event) {
        if(event.message)
            self.initView();
    });

    EventService.subscribe('tileset-selection', function (event) {
        self.selectedTiles = event.message;
    });

    RoomService.getRoomLevel($routeParams.roomId).success(function (data) {
        self.level = data;
        self.tileMap = new TileMap(
            data.tileMap.width,
            data.tileMap.height,
            data.tileMap.tileWidth,
            data.tileMap.tileHeight,
            data.tileMap.layers
        );
        self.annotations = data.annotations;
        self.sketches = data.sketches;
        self.level.tileMap = self.tileMap;
        self.objects = self.level.objects;

        EventService.send('level-loaded', self.level.tileset);
    });

    MessagingService.subscribe('/topic/editor/' + this.roomId, function (payload, headers, res) {
        var operation = payload.operation;
        if(payload.type == 'TileOperation') {
            self.refreshTiles = true;
            self.tileMap.setTileId(operation.layerIndex, operation.tiles, operation.startRow, operation.startCol);
        }
        else if(payload.type == 'AddAnnotationOperation') {
            self.refreshAnnotations = true;
            self.annotations.push(operation);
        }
        else if(payload.type == 'RemoveAnnotationOperation') {
            self.refreshAnnotations = true;
            self.annotations.splice(operation.index, 1);
        }
        else if(payload.type == 'MoveAnnotationOperation') {
            self.refreshAnnotations = true;
            self.annotations[operation.index].x = operation.x;
            self.annotations[operation.index].y = operation.y;
        }
        else if(payload.type == 'InsertLayerOperation') {
            self.tileMap.insertLayer(operation.index, operation.name);
        }
        else if(payload.type == 'DeleteLayerOperation') {
            self.tileMap.layers.splice(operation.index, 1);
        }
        else if(payload.type == 'MoveLayerOperation') {
            var layer = self.level.tileMap.layers[operation.index];
            self.level.tileMap.layers[operation.index] = self.level.tileMap.layers[operation.index + operation.dir];
            self.level.tileMap.layers[operation.index + operation.dir] = layer;
        }
        else if(payload.type == 'DrawSketchOperation') {
            self.sketches.push(
                {
                    head: {
                        x: operation.x2,
                        y: operation.y2
                    },
                    tail: {
                        x: operation.x1,
                        y: operation.y1
                    }
                }
            );
        }
        else if(payload.type == 'RemoveSketchOperation') {
            self.removeSketchLine(operation.index);
        }

    });
}

LevelEditor.prototype.commit = function () {
    var self = this;
    this.uibModal.open({
        templateUrl: 'js/templates/commitModal.html',
        size: 'sm',
        controller: function ($scope, $uibModalInstance) {
            $scope.commitMessage = '';

            $scope.cancel = function () {
                $uibModalInstance.dismiss();
            };

            $scope.ok = function () {
                self.roomService.commitRoom(self.roomId, $scope.commitMessage)
                    .success(function (data) {
                        self.alertService.addPopUpAlert('Success', 'Changes committed', 'success');
                    })
                    .error(function (data) {
                        self.alertService.addPopUpAlert('Error', data, 'danger');
                    });
                $uibModalInstance.close();
            }
        }
    });
};

LevelEditor.prototype.leaveRoom = function () {
    var self = this;
    this.roomService.disconnectFromRoom(this.roomId).success(function (data) {
        self.location.path('/rooms');
        self.alertService.addPopUpAlert('Success', 'Disconnected from room successfully', 'success')
    });
};

LevelEditor.prototype.selectLayer = function (index) {
    this.selectedLayer = index;
};

LevelEditor.prototype.deleteLayer = function (index) {
    this.sendOperation(LevelOperation.makeDeleteLayerOperation(index));
};

LevelEditor.prototype.insertLayer = function () {
    var self = this;
    this.uibModal.open({
        templateUrl: 'js/templates/insertLayerModal.html',
        size: 'sm',
        controller: function ($scope, $uibModalInstance) {
            $scope.layerName = '';

            $scope.cancel = function () {
                $uibModalInstance.dismiss();
            };
            
            $scope.ok = function () {
                self.sendOperation(LevelOperation.makeInsertLayerOperation(self.selectedLayer, $scope.layerName));
                $uibModalInstance.close();
            }
        }
    });

};

LevelEditor.prototype.moveLayer = function (index, dir) {
    this.sendOperation(LevelOperation.makeMoveLayerOperation(index, dir));
};

LevelEditor.prototype.removeAnnotation = function (index) {
    this.sendOperation(LevelOperation.makeRemoveAnnotationOperation(index));
};

LevelEditor.prototype.removeSketchLine = function (index) {
    this.sketches.splice(index, 1);
};

LevelEditor.prototype.selectTool = function (name) {
    this.toolOptions = {};
    this.activeTool = this.tools.getTool(name);
};

LevelEditor.prototype.flipHTiles = function () {

    var temp = Array.makeArray(this.selectedTiles.length,this.selectedTiles[0].length);

    var c = 0;
    for(var row = 0; row < this.selectedTiles.length; row++) {
        c = temp[0].length - 1;
        for(var col = 0; col < this.selectedTiles[0].length; col++) {
            if(this.selectedTiles[row][col] != 0)
                this.selectedTiles[row][col] = Tile.flipH(this.selectedTiles[row][col]);
            temp[row][c] = this.selectedTiles[row][col];
            c--;
        }
    }

    this.selectedTiles = temp;
    console.log(this.selectedTiles);
};

LevelEditor.prototype.flipVTiles = function () {

    var temp = Array.makeArray(this.selectedTiles.length,this.selectedTiles[0].length);

    var r = temp.length - 1;
    for(var row = 0; row < this.selectedTiles.length; row++) {
        for(var col = 0; col < this.selectedTiles[0].length; col++) {
            if(this.selectedTiles[row][col] != 0)
                this.selectedTiles[row][col] = Tile.flipV(this.selectedTiles[row][col]);
            temp[r][col] = this.selectedTiles[row][col];
        }
        r--;
    }

    this.selectedTiles = temp;
};

LevelEditor.prototype.rotateTiles = function () {
    var self = this;

    var temp = this.selectedTiles[0].map(function(col, i) {
        return self.selectedTiles.map(function(row) {
            return Tile.rotate(row[i]);
        });
    });

    this.selectedTiles = temp;
};

/* Mouse event functions */
LevelEditor.prototype.updateMouse = function (event) {
    event.offsetX = Math.floor((event.offsetX - this.viewPort.x) / this.viewPort.scale);
    event.offsetY = Math.floor((event.offsetY - this.viewPort.y) / this.viewPort.scale);

    this.mouse.set(event.offsetX, event.offsetY);
    this.mouse.setDir(event.originalEvent.movementX, event.originalEvent.movementY);
    this.cursor.set(event.offsetX, event.offsetY, this.tileMap.tileWidth, this.tileMap.tileHeight);
};

LevelEditor.prototype.mouseEvent = function (event) {
    this.activeTool.handle(event, this);
};

LevelEditor.prototype.mouseWheelEvent = function (event, delta, deltaX, deltaY) {
    if(!event.originalEvent.altKey)
        return;

    event.preventDefault();

    var prevScale = this.viewPort.scale;

    if(delta > 0) {
        this.viewPort.scale += 0.25;
        this.viewPort.scale = this.viewPort.scale > 4?4:this.viewPort.scale;
    }
    else {
        this.viewPort.scale -= 0.25;
        this.viewPort.scale = this.viewPort.scale < 0.25?0.25:this.viewPort.scale;
    }

    var newX = (this.mouse.x * this.viewPort.scale) / prevScale;
    var newY = (this.mouse.y * this.viewPort.scale) / prevScale;
    this.offsetX += (this.mouse.x - newX);
    this.offsetY += (this.mouse.y - newY);
};

LevelEditor.prototype.setShowGrid = function () {
    this.showGrid = !this.showGrid;
    this.gridGraphics.visible = this.showGrid;
};

LevelEditor.prototype.setShowAnnotations = function () {
    this.showAnnotations = !this.showAnnotations;
    this.annotationContainer.visible = this.showAnnotations;
};

LevelEditor.prototype.setShowSketches = function () {
    this.showSketches = !this.showSketches;
    this.sketchGraphics.visible = this.showSketches;
};

/* Rendering */
LevelEditor.prototype.initView = function () {
    this.renderer = PIXI.autoDetectRenderer(
        this.tileMap.tileWidth * this.tileMap.width,
        this.tileMap.tileHeight * this.tileMap.height,
        {backgroundColor: 0xEFEFEF, autoResize: true});

    $('#level-view').empty();
    $('#level-view').append(this.renderer.view);

    this.levelRenderer = new LevelRenderer(this.renderer, this.level);

    this.stage.addChild(this.levelRenderer.container);
    this.stage.addChild(this.gridGraphics);
    this.stage.addChild(this.sketchGraphics);
    this.stage.addChild(this.selectedTilesPreview);
    this.stage.addChild(this.cursorHover);
    this.stage.addChild(this.annotationContainer);

    var self = this;

    this.loaded = true;

    this.render();
};

LevelEditor.prototype.drawSketches = function () {
    this.sketchGraphics.clear();
    this.sketchGraphics.beginFill(0xDE0F0F);
    this.sketchGraphics.lineStyle(5, 0xDE0F0F, 0.6);

    var self = this;
    angular.forEach(this.level.sketches, function (value) {
        self.sketchGraphics.moveTo(value.tail.x, value.tail.y);
        self.sketchGraphics.lineTo(value.head.x, value.head.y);
    });
    this.sketchGraphics.endFill();
};

LevelEditor.prototype.drawAnnotations = function () {
    if(!this.refreshAnnotations)
        return;

    this.annotationContainer.removeChildren(0, this.annotationContainer.children.length);

    var self = this;
    var index = 0;
    angular.forEach(self.annotations, function (value) {
        var _index = index;
        var text = new PIXI.Text(value.text);
        text.x = value.x;
        text.y = value.y;
        text.interactive = true;

        text.on('mousedown', function () {
            this.dragging = true;
        });

        text.on('mouseup', function () {
            this.dragging = false;

            if(self.activeTool.name != 'annotater')
                return;

            if(self.toolOptions == 'move') {
                self.sendOperation(
                    LevelOperation.makeMoveAnnotationOperation(
                        _index,
                        this.x,
                        this.y));
            }
            else if(self.toolOptions == 'delete') {
                self.removeAnnotation(_index);
            }

        });

        text.on('mousemove', function () {
            if(self.activeTool.name != 'annotater' || self.toolOptions != 'move')
                return;

            if(!this.dragging)
                return;

            text.x += self.mouse.dx;
            text.y += self.mouse.dy;
        });


        self.annotationContainer.addChild(text);

        index++;
    });

    this.refreshAnnotations = false;
};

LevelEditor.prototype.drawGrid = function () {
    this.gridGraphics.clear();
    this.gridGraphics.beginFill(0xFF3300);
    this.gridGraphics.lineStyle(1, 0x0, 0.3);
    // draw grid
    for(var row = 0; row <= this.tileMap.height; row++) {
        this.gridGraphics.moveTo(0, row * this.tileMap.tileHeight);
        this.gridGraphics.lineTo(this.tileMap.width * this.tileMap.tileWidth, row * this.tileMap.tileHeight);
    }
    for(var col = 0; col <= this.tileMap.width; col++) {
        this.gridGraphics.moveTo(col * this.tileMap.tileWidth, 0);
        this.gridGraphics.lineTo(col * this.tileMap.tileWidth, this.tileMap.height * this.tileMap.tileHeight);
    }
    this.gridGraphics.endFill();
};

LevelEditor.prototype.drawSelectedTilesPreview = function () {
    this.selectedTilesPreview.removeChildren(0, this.selectedTilesPreview.children.length);

    if(this.activeTool.name != 'brush')
        return;

    var startRow = this.cursor.row - Math.floor(this.selectedTiles.length / 2);
    var startCol = this.cursor.col - Math.floor(this.selectedTiles[0].length / 2);

    for(var row = 0; row < this.selectedTiles.length; row++) {
        for(var col = 0; col < this.selectedTiles[0].length; col++) {
            if(this.selectedTiles[row][col] == 0)
                continue;

            var rawId = this.selectedTiles[row][col];
            var tileId = Tile.getTileID(rawId).toString();
            var tile = PIXI.Sprite.fromFrame(tileId);

            tile.position.x = (startCol + col) * this.tileMap.tileWidth;
            tile.position.y = (startRow + row) * this.tileMap.tileHeight;
            if(Tile.isFlipH(rawId)) {
                tile.scale.x = -1;
                tile.position.x += this.tileMap.tileWidth;
            }
            if(Tile.isFlipV(rawId)) {
                tile.scale.y = -1;
                tile.position.y += this.tileMap.tileHeight;
            }
            if(Tile.isRotate(rawId))
                tile.rotation = -1.5708;

            tile.alpha = 0.3;

            this.selectedTilesPreview.addChild(tile);
        }
    }
};

LevelEditor.prototype.drawCursor = function () {
    this.cursorHover.clear();
    this.cursorHover.beginFill(0x52D4FF, 0.3);
    this.cursorHover.drawRect(0, 0, this.tileMap.tileWidth, this.tileMap.tileHeight);
    this.cursorHover.endFill();
    this.cursorHover.position.set(this.cursor.col * this.tileMap.tileWidth, this.cursor.row * this.tileMap.tileHeight);
};

LevelEditor.prototype.render = function () {
    requestAnimationFrame(this.render.bind(this));

    if(!this.loaded)
        return;

    if(this.refreshTiles)
        this.levelRenderer.refresh(this.viewPort);
    this.refreshTiles = false;

    this.drawGrid();
    this.drawSketches();
    this.drawSelectedTilesPreview();
    this.drawCursor();
    this.drawAnnotations();

    this.stage.position.set(this.viewPort.x, this.viewPort.y);
    this.stage.scale.set(this.viewPort.scale);

    this.renderer.render(this.stage);
};

LevelEditor.prototype.sendOperation = function(operation) {
    this.messaging.send('/app/editor/' + this.roomId, operation);
};

function LevelOperation() {}

LevelOperation.makeTileOperation = function (layerIndex, row, col, tiles) {
    return {
        type: 'TileOperation',
        layerIndex: layerIndex,
        startRow: row,
        startCol: col,
        tiles: tiles
    };
};

LevelOperation.makeInsertLayerOperation = function (index, name) {
    return {
        type: 'InsertLayerOperation',
        index: index,
        name: name
    };
};

LevelOperation.makeMoveLayerOperation = function (index, dir) {
    return {
        type: 'MoveLayerOperation',
        index: index,
        dir: dir
    };
};

LevelOperation.makeDeleteLayerOperation = function (index) {
    return {
        type: 'DeleteLayerOperation',
        index: index
    };
};

LevelOperation.makeRemoveAnnotationOperation = function (index) {
    return {
        type: 'RemoveAnnotationOperation',
        index: index
    };
};

LevelOperation.makeMoveAnnotationOperation = function (index, x, y) {
    return {
        type: 'MoveAnnotationOperation',
        index: index,
        x: x,
        y: y
    };
};

LevelOperation.makeDrawSketchOperation = function (x1, y1, x2, y2) {
    return {
        type: 'DrawSketchOperation',
        x1: x1,
        y1: y1,
        x2: x2,
        y2: y2
    };
};

LevelOperation.makeRemoveSketchOperation = function (index) {
    return {
        type: 'RemoveSketchOperation',
        index: index
    };
};

LevelOperation.makeAddAnnotationOperation = function (text, x, y) {
    return {
        type: 'AddAnnotationOperation',
        text: text,
        x: x,
        y: y
    };
};

function LevelRenderer(renderer, level) {
    this.renderer = renderer;
    this.level = level;
    this.container = new PIXI.Container();
    // this.objectRenderer = new LevelObjectRenderer();
    this.tileMapRenderer = new TileMapRenderer(this.renderer, this.level.tileMap);

    // set viewport and stage
    this.container.addChild(this.tileMapRenderer.container);
    // this.container.addChild(this.objectRenderer.container);
}

LevelRenderer.prototype.refresh = function (viewPort) {
    this.tileMapRenderer.refresh(viewPort);
    // this.objectRenderer.refresh(viewPort);
};

/* MOUSE */
function Mouse() {
    this.x = 0;
    this.y = 0;
    this.prevX = 0;
    this.prevY = 0;
    this.dx = 0;
    this.dy = 0;
}

Mouse.prototype.set = function (x, y) {
    this.prevX = this.x;
    this.prevY = this.y;
    this.x = x;
    this.y = y;
};

Mouse.prototype.setDir = function (dx, dy) {
    this.dx = dx;
    this.dy = dy;
};

Mouse.prototype.hasMoved = function () {
    return this.prevX != this.x || this.prevY != this.y;
};

/* CURSOR */
function Cursor() {
    this.row = 0;
    this.col = 0;
    this.prevRow = 0;
    this.prevCol = 0;
}

Cursor.prototype.set = function(x, y, tileWidth, tileHeight) {
    this.prevRow = this.row;
    this.prevCol = this.col;
    this.row = Math.floor(y / tileHeight);
    this.col = Math.floor(x / tileWidth);
};

Cursor.prototype.hasMoved = function () {
    return this.prevRow != this.row || this.prevCol != this.col;
};

/* VIEWPORT */
function ViewPort() {
    this.x = 0;
    this.y = 0;
    this.scale = 1;
}

function Tile() {}

/* Tile transformation */
Tile.BITMASK_FLIPH = 0x80000000;
Tile.BITMASK_FLIPV = 0x40000000;
Tile.BITMASK_ROTATED = 0x20000000;

Tile.getTileID = function(tile) {
    tile <<= 3;
    tile >>>= 3;

    return tile;
};

Tile.isFlipH = function (tile) {
    return  ((tile & Tile.BITMASK_FLIPH) == -Tile.BITMASK_FLIPH);
};

Tile.isFlipV = function (tile) {
    return  ((tile & Tile.BITMASK_FLIPV) == Tile.BITMASK_FLIPV);
};

Tile.isRotate = function (tile) {
    return  ((tile & Tile.BITMASK_ROTATED) == Tile.BITMASK_ROTATED);
};


Tile.flipH = function (tile) {
    return tile ^ Tile.BITMASK_FLIPH;
};

Tile.flipV = function (tile) {
    return tile ^ Tile.BITMASK_FLIPV;
};

Tile.rotate = function (tile) {
    return tile ^ Tile.BITMASK_ROTATED;
};

Array.makeArray = function(numRow, numCol) {
    var temp = [];
    for(var row = 0; row < numRow; row++) {
        var array = [];

        for(var col = 0; col < numCol; col++) {
            array.push(0);
        }

        temp.push(array);
    }

    return temp;
};