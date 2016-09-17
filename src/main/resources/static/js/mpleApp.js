(function () {

    var app = angular.module('mpleApp', ['ngRoute', 'ui.bootstrap', 'ngAnimate', 'ngCookies', 'ngSanitize', 'monospaced.mousewheel', 'cfp.hotkeys']);
    
    app.controller('MainCtrl', function ($scope, $location) {
       /* var url = '/marco';
        var sock = new SockJS(url);

        function sayMarco() {
            sock.send('I love you, Vivien!');
        }

        sock.onopen = function () {
            console.log('Socket opened');
        };

        sock.onclose = function () {
            console.log('Socket closed');
        };

        sock.onmessage = function (message) {
            console.log('Received message: ' + message.data);
            setTimeout(sayMarco, 2000);
        };
        */
        $scope.go = function (path) {
            $location.path(path)
        }
    });

    app.controller('HomeCtrl', function ($interval) {
        
    });

    app.controller('LoginCtrl', function ($scope, AuthService) {
        $scope.credentials = {};

        $scope.login = function () {
            AuthService.authenticate($scope.credentials.username, $scope.credentials.password);
            $scope.credentials = {};
        };
    });

    app.controller('ProjectsCtrl', function ($scope, $log, $uibModal, ProjectService, AlertService) {
        $scope.selectProject = function (project) {
           $scope.selectedProject = project;
        };

        $scope.selectedProject = null;

        $scope.refresh = function () {
            ProjectService.listProjects().success(function (data) {
                $scope.projects = data;
            });
        };

        $scope.addProject = function () {
            $uibModal.open({
                templateUrl: 'js/templates/createProjectModal.html',
                size: 'sm',
                controller: function ($scope, $uibModalInstance) {
                    $scope.projectName = '';

                    $scope.cancel = function () {
                        $uibModalInstance.dismiss();
                    };

                    $scope.add = function () {
                        ProjectService.createProject($scope.projectName)
                            .success(function (data) {
                                AlertService.addPopUpAlert('Success', data, 'success');
                                $scope.refresh();
                            })
                            .error(function (data) {
                                AlertService.addPopUpAlert('Error', data, 'danger');
                            });
                        $uibModalInstance.close();
                    }

                },
                scope: $scope
            });
        };

        $scope.deleteProject = function () {

            if($scope.selectedProject == null)
                return;

            AlertService.addConfirmationAlert(
                'Delete Project',
                'Are you sure you want to delete project \'' + $scope.selectedProject.name +
                '\'? This cannot be undone and all the levels of the project will be lost.',
                function () {
                    ProjectService.deleteProject($scope.selectedProject.name)
                        .success(function (data) {
                            $scope.refresh();
                            AlertService.addPopUpAlert('Success', data, 'success');
                        })
                        .error(function (data) {
                            AlertService.addPopUpAlert('Error', data, 'danger');
                        })
                },
                function () {});
        };
        
        $scope.refresh();
    });

    app.controller('ProjectDetailsCtrl', function ($scope, $routeParams, $uibModal, $log,
                                                   ProjectService, LevelService, TilesetService,
                                                   AlertService, ErrorFormatter) {


        $scope.selectedLevel = null;

        $scope.selectLevel = function (level) {
          $scope.selectedLevel = level;
        };

        $scope.refresh = function () {
            ProjectService.getProject($routeParams.name).success(function (data) {
                $scope.project = data;
            });

            LevelService.listLevels($routeParams.name).success(function (data) {
                $scope.levels = data;
            });

            TilesetService.listTilesets().success(function (data) {
                $scope.tilesets = data;
            });
        };

        $scope.refresh();

        $scope.deleteLevel = function () {

            if($scope.selectedLevel == null)
                return;

            AlertService.addConfirmationAlert('Delete level', 'Are you sure you want to delete \'' +  $scope.selectedLevel.name + '\'?',
                function () {
                    LevelService.deleteLevel($routeParams.name, $scope.selectedLevel.name)
                        .success(function (data) {
                            AlertService.addPopUpAlert('Success', data, 'success');
                            $scope.refresh();
                        })
                        .error(function (data) {
                            AlertService.addPopUpAlert('Error', data, 'danger');
                        })
                }, function () {});
        };

        $scope.add = function () {
            $uibModal.open({
                templateUrl: 'js/templates/createLevelModal.html',
                controller: function ($scope, $uibModalInstance) {
                    $scope.level = {};

                    $scope.cancel = function () {
                      $uibModalInstance.dismiss();
                    };

                    $scope.add = function () {
                        LevelService.createLevel($routeParams.name, $scope.level)
                            .success(function (data) {
                                AlertService.addPopUpAlert('Success', data, 'success');
                                $scope.refresh();
                            })
                            .error(function (data) {
                                AlertService.addPopUpAlert('Error: could not create the level', ErrorFormatter.format(data), 'danger');
                            });
                        $uibModalInstance.close();
                    }
                    
                },
                scope: $scope
            });
        };
    });

    app.controller('LevelEditorCtrl', LevelEditor);

    app.controller('TilesetCtrl', function ($scope, TilesetService, EventService) {

        EventService.subscribe('level-loaded', function (event) {
            TilesetService.getTileset(event.message.tilesetId).success(loadTileset);
        });

        var tileset;
        var selectedTiles = [];

        function loadTileset(data) {
            tileset = new Tileset(
                data.tileset.offsetX,
                data.tileset.offsetY,
                data.tileset.tileWidth,
                data.tileset.tileHeight,
                data.tileset.numRow,
                data.tileset.numCol,
                data.tileset.spacing
            );

            var img = new Image();
            img.src = 'data:image/png;base64,' + data.img;
            var baseTexture = new PIXI.BaseTexture(img);
            baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;

            var id = 1;
            for(var row = 0; row < tileset.numRow; row++) {
                for(var col = 0; col < tileset.numCol; col++) {
                    PIXI.Texture.addTextureToCache(new PIXI.Texture(baseTexture,
                        new PIXI.Rectangle(
                            tileset.offsetX + col * (tileset.tileWidth + tileset.spacing),
                            tileset.offsetY + row * (tileset.tileHeight + tileset.spacing),
                            tileset.tileWidth,
                            tileset.tileHeight)), id.toString());
                    id++;
                }
            }

            EventService.send('tileset-loaded', true);

            initView();
        }

        /* MOUSE */
        var cursor = {
            row: 0,
            col: 0,
            prevRow: 0,
            prevCol: 0,
            set: function (x, y) {
                this.prevRow = this.row;
                this.prevCol = this.col;
                this.row = Math.floor(y / tileset.tileHeight);
                this.col = Math.floor(x / tileset.tileWidth);
                this.row = Math.max(0, this.row);
                this.row = Math.min(tileset.numRow - 1, this.row);
                this.col = Math.max(0, this.col);
                this.col = Math.min(tileset.numCol - 1, this.col);
            },
            hasMoved: function () {
                return this.prevRow != this.row || this.prevCol != this.col;
            }
        };

        $scope.updateCursor = function (event) {
            cursor.set(event.offsetX, event.offsetY);
        };

        var renderer;
        var viewport = new PIXI.Container();
        var gridContainer = new PIXI.Container();
        var selectedTilesHover = new PIXI.Graphics();
        var tilesetRenderer;
        var cursorShape;
        var offsetX = 0;
        var offsetY = 0;
        var scale = 1;

        function initView() {
            renderer = PIXI.autoDetectRenderer($('#tileset-view').width() - 8, $('#tileset-view').width(), {backgroundColor: 0xEFEFEF, autoResize: true});
            $('#tileset-view').append(renderer.view);

            $('#tileset-view').onresize = function (event){
                var view = $('#tileset-view');
                renderer.resize(view.width(), view.width());
            };


            cursorShape = new PIXI.Graphics(0, 0, tileset.tileWidth, tileset.tileHeight);
            gridContainer.addChild(cursorShape);
            cursorShape.beginFill(0x52D4FF, 0.3);
            cursorShape.drawRect(0, 0, tileset.tileWidth, tileset.tileHeight);
            cursorShape.endFill();

            // draw gridseparator
            var graphics = new PIXI.Graphics();
            graphics.clear();
            graphics.beginFill(0xFF3300, 0.5);
            graphics.lineStyle(1, 0x0, 0.3);
            for(var row = 0; row <= tileset.numRow; row++) {
                graphics.moveTo(0, row * tileset.tileHeight);
                graphics.lineTo(tileset.numCol * tileset.tileWidth, row * tileset.tileHeight);
            }
            for(var col = 0; col <= tileset.numCol; col++) {
                graphics.moveTo(col * tileset.tileWidth, 0);
                graphics.lineTo(col * tileset.tileWidth, tileset.numRow * tileset.tileHeight);
            }
            graphics.endFill();
            gridContainer.addChild(graphics);
            gridContainer.addChild(selectedTilesHover);

            tilesetRenderer = new TilesetRenderer(renderer, tileset);
            viewport.addChild(tilesetRenderer.container);
            viewport.addChild(gridContainer);

            render();
        }

        function drawSelectedTilesHover() {
            selectedTilesHover.clear();
            selectedTilesHover.beginFill(0x52D4FF, 0.5);
            selectedTilesHover.drawRect(
                firstPos.col * tileset.tileWidth,
                firstPos.row * tileset.tileHeight,
                (Math.abs(firstPos.col - secondPos.col) + 1) * tileset.tileWidth,
                (Math.abs(firstPos.row - secondPos.row) + 1) * tileset.tileHeight);
            selectedTilesHover.endFill();
        }

        function render() {
            requestAnimationFrame(render);
            drawSelectedTilesHover();
            cursorShape.position.set(cursor.col * tileset.tileWidth, cursor.row * tileset.tileHeight);
            tilesetRenderer.refresh(offsetX, offsetY, scale);
            renderer.render(viewport);
        }

        var firstPos = {row: 0, col: 0};
        var secondPos = {row: 2, col: 2};

        $scope.mouseDown = function (event) {
            firstPos = {row: cursor.row, col: cursor.col};
            secondPos = {row: cursor.row, col: cursor.col};
        };

        $scope.mouseUp = function (event) {
            secondPos = {row: cursor.row, col: cursor.col};

            if(firstPos.col - secondPos.col > 0) {
                firstPos.col += secondPos.col;
                secondPos.col = firstPos.col - secondPos.col;
                firstPos.col -= secondPos.col;
            }

            if(firstPos.row - secondPos.row > 0) {
                firstPos.row += secondPos.row;
                secondPos.row = firstPos.row - secondPos.row;
                firstPos.row -= secondPos.row;
            }

            var tiles = [];

            for(var row = firstPos.row; row <= secondPos.row; row++) {
                var temp = [];
                for(var col = firstPos.col; col <= secondPos.col; col++) {
                    temp.push(tileset.getId(row, col));
                }

                tiles.push(temp);
            }

            selectedTiles = tiles;

            EventService.send('tileset-selection', selectedTiles);
        };
    });

    app.factory('ProjectService', function ($http) {
        return {
            getProject: getProject,
            listProjects: listProjects,
            createProject: createProject,
            deleteProject: deleteProject
        };

        function getProject(name) {
            return $http.get('/api/projects/' + name);
        }

        function listProjects() {
            return $http.get('/api/projects');
        }

        function createProject(name) {
            return $http.post('/api/projects', name);
        }

        function deleteProject(name) {
            return $http.delete('/api/projects/' + name)
        }
    });
    
    app.factory('LevelService', function ($http) {
        return {
            getLevel: getLevel,
            listLevels: listLevels,
            createLevel: createLevel,
            deleteLevel: deleteLevel
        };

        function getLevel(id) {
            return $http.get('/api/projects/project/levels/' + id);
        }
        
        function listLevels(projectName) {
            return $http.get('/api/projects/' + projectName + '/levels');
        }

        function createLevel(projectName, body) {
            return $http.post('/api/projects/' + projectName + '/levels', body);
        }

        function deleteLevel(projectName, levelName) {
            return $http.delete('/api/projects/' + projectName + '/levels/' + levelName);
        }
    });

    app.factory('TilesetService', function ($http) {

        return {
            getTileset: getTileset,
            listTilesets: listTilesets
        };

        function getTileset(id) {
            return $http.get('/api/tilesets/' + id);
        }

        function listTilesets() {
            return $http.get('/api/tilesets');
        }

    });

    app.factory('AlertService', function ($http, $uibModal) {

        var alerts = [];

        return {
            addAlert: addAlert,
            addPopUpAlert: addPopUpAlert,
            addConfirmationAlert: addConfirmationAlert,
            getAlerts: getAlerts,
            closeAlert: closeAlert
        };

        function addAlert(message, type) {
            var date = new Date();

            alerts.push({
                msg: message,
                type: type,
                timestamp: new Date().toTimeString().substring(0, 8)
            })
        }

        function addPopUpAlert(title, message, type) {
            $uibModal.open({
                templateUrl: 'js/templates/alertModal.html',
                controller: function ($scope, $uibModalInstance) {
                    $scope.alert = {
                        title: title,
                        msg: message,
                        type: type
                    };

                    $scope.close = function () {
                        $uibModalInstance.dismiss();
                    };
                }
            });
        }

        function addConfirmationAlert(title, message, okCallback, cancelCallback) {
            $uibModal.open({
                templateUrl: 'js/templates/confirmationModal.html',
                size: 'sm',
                controller: function ($scope, $uibModalInstance) {
                    $scope.alert = {
                        title: title,
                        msg: message
                    };

                    $scope.cancel = function () {
                        cancelCallback.call();
                        $uibModalInstance.dismiss();
                    };

                    $scope.ok = function () {
                        okCallback.call();
                        $uibModalInstance.close()
                    };
                }
            });
        }

        function getAlerts() {
            return alerts;
        }

        function closeAlert(index) {
            alerts.splice(index, 1);
        }

    });

    app.factory('ErrorFormatter', function () {

        return {
          format: format
        };

        function format(errorMsg) {

            var message = '';

            if(errorMsg.hasOwnProperty('errors')) {
                message = '<ul>';
                angular.forEach(errorMsg.errors, function (value) {
                    message += ('<li>' + value.field + ' ' + value.defaultMessage + '</li>');
                });
                message += '</ul>';
            }
            else
                message = errorMsg;

            return message;
        }

    });
    
    app.factory('Tools', function () {

        var tools = {};

        return {
            addTool: addTool,
            getTool: getTool,
            listTools: listTools
        };
        
        function addTool(name, tooltip, icon, handle) {
            tools[name] = {
                name: name,
                tooltip: tooltip,
                icon: icon,
                handle: handle
            }
        }

        function getTool(name) {
            return tools[name];
        }
        
        function listTools() {
            return tools;
        }

    });

    app.factory('EventService', function () {

        var eventTypeToSubscribers = {};

        return {
            subscribe: subscribe,
            send: send
        };

        function subscribe(eventType, callback) {
            if(eventTypeToSubscribers[eventType] == undefined)
                eventTypeToSubscribers[eventType] = [];

            eventTypeToSubscribers[eventType].push(callback);
        }

        function send(eventType, message) {
            var event = {eventType: eventType, message: message};
            var subscribers = eventTypeToSubscribers[eventType];
            for(var index = 0; index < subscribers.length; index++) {
                subscribers[index](event);
            }
        }

    });

    app.factory('AuthService', function ($cookies, $http, AlertService, $location) {
        return {
            isAuthenticated: isAuthenticated,
            authenticate: authenticate,
            logout: logout
        };
        
        function isAuthenticated() {
            return $cookies.get('authenticated') == 'true';
        }

        function authenticate(username, password) {
            $http.post(
                '/login',
                'username='+username+'&'+'password='+password,
                {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
                .success(function () {
                    $cookies.put('authenticated', 'true');
                    AlertService.addPopUpAlert('Login successful', 'Welcome.', 'success');
                    $location.path('/');
                })
                .error(function () {
                    AlertService.addPopUpAlert('Login failed', 'Username or password incorrect.', 'info');
                });
        }
        
        function logout() {
            $http.post('/logout', '')
                .success(function () {
                    $cookies.put('authenticated', 'false');
                    AlertService.addPopUpAlert('Logout successful', 'Welcome.', 'success');
                    $location.path('/login');
                })
                .error(function () {
                    AlertService.addPopUpAlert('Logout failed', 'Could not logout', 'info');
                });
        }
    });

    app.directive('navBar', function (AuthService) {
        return {
            restrict: 'E',
            templateUrl: 'js/directives/nav-bar.html',
            link: function (scope) {
                scope.logout = function () {
                    AuthService.logout();
                };
            }
        };
    });

    app.directive('alerts', function (AlertService) {

        return {
            restrict: 'E',
            templateUrl: 'js/directives/alerts.html',
            link: function (scope) {
                scope.alerts = AlertService.getAlerts();

                scope.closeAlert = function (index) {
                    AlertService.closeAlert(index);
                };
            }
        };
    });

    app.directive('ngMousedrag', ['$document', function ngMousedrag($document) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var endTypes = 'touchend mouseup'
                    , moveTypes = 'touchmove mousemove'
                    , startTypes = 'touchstart mousedown'
                    , startX, startY;

                element.bind(startTypes, function startDrag(e) {
                    e.preventDefault();
                    startX = e.pageX;
                    startY = e.pageY;

                    $document.bind(moveTypes, function (e) {
                        e.dragX = e.pageX - startX;
                        e.dragY = e.pageY - startY;
                        e.startX = startX;
                        e.startY = startY;
                        scope.$event = e;
                        scope.$eval(attrs.ngMousedrag);
                    });

                    $document.bind(endTypes, function () {
                        $document.unbind(moveTypes);
                    });
                });
            }
        };
    }]);

    app.config(function($routeProvider, $httpProvider) {

        $httpProvider.interceptors.push(function($q, $cookies, $location) {
            return {
                'responseError': function(response) {
                    if(response.status == 401) {
                        $cookies.put('authenticated', 'false');
                        $location.path('/login')
                    }

                    return response;
                }
            };
        });

        $routeProvider
            .when('/', {
                templateUrl: 'js/views/home.html',
                controller: 'HomeCtrl'
            })
            .when('/login', {
                templateUrl: 'js/views/login.html',
                controller: 'LoginCtrl'
            })
            .when('/projects', {
                templateUrl: 'js/views/projects.html',
                controller: 'ProjectsCtrl'
            })
            .when('/projects/:name', {
                templateUrl: 'js/views/project-details.html',
                controller: 'ProjectDetailsCtrl'
            })
            .when('/level-editor/:roomId', {
                templateUrl: 'js/views/level-editor.html',
                controller: 'LevelEditorCtrl',
                controllerAs: 'editor'
            })
            .otherwise({redirectTo: '/'});

    });

    app.run(function($rootScope, $location, AuthService) {
        $rootScope.$on( "$routeChangeStart", function(event, next, current) {
            if(!AuthService.isAuthenticated())
                $location.path('/login');
            else if(next.templateUrl == 'js/views/login.html')
                $location.path('/');
        });
    });
})();