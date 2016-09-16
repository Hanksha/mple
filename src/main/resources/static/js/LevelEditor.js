function LevelEditor($routeParams, hotkeys, Tools, LevelService, TilesetLoader, EventService) {
    var self = this;
    this.loaded = false;
    this.world = null;
    this.tileMap = null;
    this.objects = null;

    this.tools = Tools;
    this.mouse = new Mouse();
    this.cursor = new Cursor();
    this.viewPort = new ViewPort();
    this.activeTool = Tools.getTool('brush');
    this.selectedTiles = [[0]];
    this.selectedLayer = 0;

    this.renderer = null;
    this.stage = new PIXI.Container();
    this.selectedTilesPreview = new PIXI.Container();
    this.gridGraphics = new PIXI.Graphics();
    this.cursorHover = new PIXI.Graphics();
    this.levelRenderer = null;

    // Keyboard shortcuts
    hotkeys.add({
        combo: 'shift+left',
        callback: function() {
            this.viewPort.x -= 10;
        }
    });
    hotkeys.add({
        combo: 'shift+right',
        callback: function() {
            this.viewPort.x += 10;
        }
    });
    hotkeys.add({
        combo: 'shift+up',
        callback: function() {
            this.viewPort.y -= 10;
        }
    });
    hotkeys.add({
        combo: 'shift+down',
        callback: function() {
            this.viewPort.y += 10;
        }
    });

    EventService.subscribe('tileset-loaded', function (event) {
        if(event.message)
            self.initView();
    });

    EventService.subscribe('tileset-selection', function (event) {
        self.selectedTiles = event.message;
    });

    LevelService.getWorld($routeParams.roomId).success(function (data) {
        self.world = data;
        self.tileMap = new TileMap(
            data.tileMap.width,
            data.tileMap.height,
            data.tileMap.tileWidth,
            data.tileMap.tileHeight,
            data.tileMap.layers
        );

        self.objects = self.world.objects;

        EventService.send('world-loaded', self.world);
    });
}

LevelEditor.prototype.selectLayer = function (index) {
    this.selectedLayer = index;
};

LevelEditor.prototype.deleteLayer = function (index) {
    this.tileMap.layers.splice(index, 1);
};

LevelEditor.prototype.selectTool = function (name) {
    this.activeTool = this.tools.getTool(name);
};

/* Mouse event functions */
LevelEditor.prototype.updateMouse = function (event) {
    event.offsetX = Math.floor((event.offsetX - this.viewPort.x) / this.viewPort.scale);
    event.offsetY = Math.floor((event.offsetY - this.viewPort.y) / this.viewPort.scale);

    this.mouse.set(event.offsetX, event.offsetY);
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

/* Rendering */
LevelEditor.prototype.initView = function () {
    this.renderer = PIXI.autoDetectRenderer($('#level-view').width() - 8, $(window).height(), {backgroundColor: 0xEFEFEF, autoResize: true});
    $('#level-view').append(this.renderer.view);

    this.levelRenderer = new WorldRenderer(this.renderer, this.world);

    this.stage.addChild(this.levelRenderer.container);
    this.stage.addChild(this.gridGraphics);
    this.stage.addChild(this.selectedTilesPreview);
    this.stage.addChild(this.cursorHover);

    var self = this;

    window.onresize = function (event){
        var view = $('#world-view');
        self.renderer.resize(view.width(), $(window).height());
    };

    this.loaded = true;

    this.render();
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
    var startRow = this.cursor.row - Math.floor(this.selectedTiles.length / 2);
    var startCol = this.cursor.col - Math.floor(this.selectedTiles[0].length / 2);

    for(var row = 0; row < this.selectedTiles.length; row++) {
        for(var col = 0; col < this.selectedTiles[0].length; col++) {
            if(this.selectedTiles[row][col] == 0)
                continue;

            var tile = PIXI.Sprite.fromFrame(this.selectedTiles[row][col].toString());
            tile.alpha = 0.3;
            tile.position.x = (startCol + col) * this.tileMap.tileWidth;
            tile.position.y = (startRow + row) * this.tileMap.tileHeight;

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

    this.levelRenderer.refresh(this.viewPort);
    this.drawGrid();
    this.drawSelectedTilesPreview();
    this.drawCursor();

    this.stage.position.set(this.viewPort.x, this.viewPort.y);
    this.stage.scale.set(this.viewPort.scale);

    this.renderer.render(this.stage);
};

function LevelRenderer(renderer, world) {
    this.renderer = renderer;
    this.world = world;
    this.container = new PIXI.Container();
    // this.objectRenderer = new WorldObjectRenderer();
    this.tileMapRenderer = new TileMapRenderer(this.renderer, this.world.tileMap);

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
}

Mouse.prototype.set = function (x, y) {
    this.prevX = this.x;
    this.prevY = this.y;
    this.x = x;
    this.y = y;
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