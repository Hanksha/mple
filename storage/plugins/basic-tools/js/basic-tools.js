var app = angular.module('mpleApp');

app.run(function (Tools) {

    Tools.addTool(
        'brush',
        'Brush Tool',
        'paint-brush',
        handle
    );

    function handle(event, editor) {
        editor.tileMap.setTileId(editor.selectedLayer, editor.selectedTiles, editor.cursor.row, editor.cursor.col);
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
        editor.tileMap.setTileId(editor.selectedLayer, [[0]], editor.cursor.row, editor.cursor.col);
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
