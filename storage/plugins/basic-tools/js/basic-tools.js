var app = angular.module('mpleApp');

app.run(function (Tools) {

    Tools.addTool(
        'brush',
        'Brush Tool',
        'paint-brush',
        handle
    );

    function handle(event, editor) {
        var tiles = editor.selectedTiles;
        var startRow = editor.cursor.row - Math.floor(tiles.length / 2);
        var startCol = editor.cursor.col - Math.floor(tiles[0].length / 2);
        
        editor.messaging.send('/app/editor/' + editor.roomId, {
            type: 'tileOperation',
            layerIndex: editor.selectedLayer,
            startRow: startRow,
            startCol: startCol,
            tiles: tiles
        });
    }
});

app.run(function (Tools) {

    Tools.addTool(
        'eraser',
        'Eraser Tool',
        'eraser',
        handle
    );

    function handle(event, editor) {
        editor.messaging.send('/app/editor/' + editor.roomId, {
            type: 'tileOperation',
            layerIndex: editor.selectedLayer,
            startRow: editor.cursor.row,
            startCol: editor.cursor.col,
            tiles: [[0]]
        });
    }
});

app.run(function (Tools) {

    Tools.addTool(
        'filler',
        'Filler Tool',
        'tint',
        handle
    );

    function handle(event, editor) {
    }
});

app.run(function (Tools) {

    Tools.addTool(
        'tilePicker',
        'Tile Picker Tool',
        'eyedropper',
        handle
    );

    function handle(event, editor) {
    }
});

app.run(function (Tools) {

    Tools.addTool(
        'sketcher',
        'Sketching Tool',
        'pencil-square-o',
        handle
    );

    function handle(event, editor) {
    }
});

app.run(function (Tools) {

    Tools.addTool(
        'annotater',
        'Annotation Tool',
        'commenting',
        handle
    );

    function handle(event, editor) {
    }
});
