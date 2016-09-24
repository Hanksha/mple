var app = angular.module('mpleApp');

app.run(function (Tools) {

    Tools.addTool(
        'brush',
        'Brush Tool',
        'paint-brush',
        handle
    );

    function handle(event, editor) {
        event.preventDefault();
        var tiles = editor.selectedTiles;
        var startRow = editor.cursor.row - Math.floor(tiles.length / 2);
        var startCol = editor.cursor.col - Math.floor(tiles[0].length / 2);

        editor.sendOperation(
            LevelOperation.makeTileOperation(editor.selectedLayer, startRow, startCol, tiles)
        );
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
        editor.sendOperation(
            LevelOperation.makeTileOperation(
                editor.selectedLayer,
                editor.cursor.row,
                editor.cursor.col,
                [[0]]
            )
        );
    }
});

app.run(function (Tools) {

    /*Tools.addTool(
        'filler',
        'Filler Tool',
        'tint',
        handle
    );

    function handle(event, editor) {
    }*/
});

app.run(function (Tools) {

    Tools.addTool(
        'tilePicker',
        'Tile Picker Tool',
        'eyedropper',
        handle
    );

    function handle(event, editor) {
        var layer = editor.tileMap.layers[editor.selectedLayer];
        var row = editor.cursor.row;
        var col = editor.cursor.col;
        editor.selectedTiles = [[layer.grid[row][col]]];
    }
});

app.run(function (Tools) {

    /*Tools.addTool(
        'sketcher',
        'Sketching Tool',
        'pencil-square-o',
        handle
    );

    function handle(event, editor) {
        
    }*/
});

app.run(function (Tools) {

    Tools.addTool(
        'annotater',
        'Annotation Tool',
        'commenting',
        handle
    );

    function handle(event, editor) {
        editor.uibModal.open({
            templateUrl: 'plugins/basic-tools/templates/addAnnotationModal.html',
            size: 'sm',
            controller: function ($scope, $uibModalInstance) {
                $scope.text = '';
                $scope.ok = function () {
                    editor.sendOperation(
                        LevelOperation.makeAddAnnotationOperation($scope.text, editor.mouse.x, editor.mouse.y));
                    $uibModalInstance.close();
                };

                $scope.cancel = function () {
                    $uibModalInstance.dismiss();
                };
            }

        });
    }
});
