var app = angular.module('mpleApp');

app.run(function (Tools) {

    Tools.addTool(
        'brush',
        'Brush Tool',
        'paint-brush',
        handle
    );

    function handle(event, editor) {
        if(event.type == 'mouseup' || event.type == 'click')
            return;

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
        if(event.type == 'mouseup' || event.type == 'click')
            return;

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

    var x1 = -1;
    var y1 = -1;

    Tools.addTool(
        'sketcher',
        'Sketching Tool',
        'pencil-square-o',
        handle,
        'plugins/basic-tools/templates/sketcher-options.html'
    );

    function handle(event, editor) {
        if(editor.toolOptions == 'erase' && (event.type == 'click' || event.type == 'mousemove')) {
            angular.forEach(editor.sketches, function (value, index) {
               if(pDistance(
                       editor.mouse.x, editor.mouse.y,
                       value.tail.x, value.tail.y,
                       value.head.x, value.head.y) < 15) {
                   editor.sendOperation(LevelOperation.makeRemoveSketchOperation(index))
               }
            });
        }

        if(event.type == 'mousedown') {
            x1 = editor.mouse.x;
            y1 = editor.mouse.y;
        }
        else if(event.type == 'mouseup' && x1 != -1 && y1 != -1) {

            if(editor.toolOptions != 'draw')
                return;

            editor.sendOperation(
                LevelOperation.makeDrawSketchOperation(x1, y1, editor.mouse.x, editor.mouse.y)
            );
        }
    }

    function pDistance(x, y, x1, y1, x2, y2) {

        var A = x - x1;
        var B = y - y1;
        var C = x2 - x1;
        var D = y2 - y1;

        var dot = A * C + B * D;
        var len_sq = C * C + D * D;
        var param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        var xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        var dx = x - xx;
        var dy = y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
});

app.run(function (Tools) {

    Tools.addTool(
        'annotater',
        'Annotation Tool',
        'commenting',
        handle,
        'plugins/basic-tools/templates/annotater-options.html'
    );

    function handle(event, editor) {
        if(event.type != 'click')
            return;

        if(editor.toolOptions == 'add') {
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

    }
});
