angular.module('peppolApp').directive('fileActions', function () {
    return {
        restrict: 'E',
        scope: {
            file: '='
        },
        templateUrl: '/views/file-actions.html',
        link: function (scope) {

        }
    }
});

function setActions(file, status) {

}

function setStatus($scope, status) {
    if (status === "sent") {
        $scope.label = 'label-success';
        $scope.status = 'delivered';
    } else if (status === "resolved") {
        $scope.label = 'label-success';
        $scope.status = 'resolved';
    } else if (status === "failed") {
        $scope.label = 'label-danger';
        $scope.status = 'failed';
    } else if (status === "invalid") {
        $scope.label = 'label-danger';
        $scope.status = 'invalid';
    } else if (status === "reprocessed") {
        $scope.label = 'label-primary';
        $scope.status = 'reprocessing';
    } else {
        $scope.label = 'label-primary';
        $scope.status = 'processing';
    }
}

angular.module('peppolApp').directive('messageStatus', function () {
    return {
        restrict: 'E',
        scope: {
            msg: '='
        },
        template: '<span class="label {{label}}">{{status}}</span>',
        link: function ($scope) {
            setStatus($scope, $scope.msg.status);
        }
    }
});

angular.module('peppolApp').directive('fileStatus', function () {
    return {
        restrict: 'E',
        scope: {
            file: '=',
            direction: '='
        },
        template: '<span class="label {{label}}">{{status}}</span>',
        link: function ($scope) {
            console.log("Direction: " + $scope.direction);
            if (typeof $scope.file.status != 'undefined') {
                if ($scope.file.status != 'resolved') {
                    setStatus($scope, $scope.file.status);
                    return;
                }
            }
            var status, ts;

            if (typeof $scope.file.sent != 'undefined') {
                status = "sent";
                ts = Date.parse($scope.file.sent[0].ts);
            }
            if (typeof $scope.file.failed != 'undefined' && (status !== "sent" || $scope.direction === "IN")) {
                if ($scope.file.failed[0].invalid == true) {
                    status = "invalid";
                } else {
                    status = "failed";
                }
                ts = Date.parse($scope.file.failed[0].ts);
            }
            if (typeof $scope.file.reprocessed != 'undefined') {
                if (typeof ts != 'undefined') {
                    var reprocessedTs = Date.parse($scope.file.reprocessed[0].ts);
                    if (reprocessedTs > ts) {
                        status = "reprocessed"
                    }
                } else {
                    status = "reprocessed";
                }
            }
            if (typeof status === 'undefined') {
                status = "processing";
            }
            setStatus($scope, status);
            $scope.file.status = status;
        }
    }
});

angular.module('peppolApp').directive('fileDetails', function () {
    return {
        restrict: 'E',
        scope: {
            file: '=',
            msg: '=',
            getErrorMessage: '&'
        },
        templateUrl: '/views/file-detailed-view.html',
        link: function (scope) {
            scope.showDetails = false;
            scope.toogleShow = function () {
                scope.showDetails = !scope.showDetails;
            };
            scope.events = [];
            if (typeof scope.file.sent != 'undefined') {
                for (var i = 0; i < scope.file.sent.length; i++) {
                    scope.file.sent[i].type = "sent";
                }
                scope.events = scope.events.concat(scope.file.sent);
            }
            if (typeof scope.file.failed != 'undefined') {
                for (var i = 0; i < scope.file.failed.length; i++) {
                    scope.file.failed[i].type = "failed";
                }
                scope.events = scope.events.concat(scope.file.failed);
            }
            if (typeof scope.file.reprocessed != 'undefined') {
                for (var i = 0; i < scope.file.reprocessed.length; i++) {
                    scope.file.reprocessed[i].type = "reprocessed";
                }
                scope.events = scope.events.concat(scope.file.reprocessed);
            }
            function compare(a, b) {
                if (Date.parse(a.ts) > Date.parse(b.ts)) {
                    return -1;
                }
                if (Date.parse(a.ts) < Date.parse(b.ts)) {
                    return 1;
                }
                return 0;
            }

            scope.events.sort(compare);
        }
    }
});

angular.module('peppolApp').directive('loadingContainer', function () {
    return {
        restrict: 'A',
        scope: false,
        link: function (scope, element, attrs) {
            var loadingLayer = angular.element('<div class="loading"></div>');
            element.append(loadingLayer);
            element.addClass('loading-container');
            scope.$watch(attrs.loadingContainer, function (value) {
                loadingLayer.toggleClass('ng-hide', !value);
            });
        }
    };
});

angular.module('peppolApp').directive('navMenu', function ($location, FailedFactory, InvalidFactory, InboundInvalidFactory, ProcessingFactory, MessageFactory, InboundMessageFactory, ReprocessedFactory) {
    return function (scope, element, attrs) {
        var links = element.find('a'),
            onClass = attrs.navMenu || 'on',
            routePattern,
            link,
            url,
            currentLink,
            urlMap = {},
            i;

        if (!$location.$$html5) {
            routePattern = /^#[^/]*/;
        }

        for (i = 0; i < links.length; i++) {
            link = angular.element(links[i]);
            url = link.attr('href');

            if ($location.$$html5) {
                urlMap[url] = link;
            } else {
                urlMap[url.replace(routePattern, '')] = link;
            }
        }

        scope.$on('$routeChangeStart', function () {
            var pathLink = urlMap[$location.path()];

            if (pathLink) {
                if (currentLink) {
                    currentLink.parent("li").removeClass(onClass);
                }
                currentLink = pathLink;
                currentLink.parent("li").addClass(onClass);
            }
            MessageFactory.size(function (data) {
                scope.outAllCount = data.length;
            });

            FailedFactory.size(function (data) {
                scope.outFailedCount = data.length;
            });

            InvalidFactory.size(function (data) {
                scope.outInvalidCount = data.length;
            });

            ProcessingFactory.size(function (data) {
                scope.outProcessingCount = data.length;
            });

            ReprocessedFactory.size(function (data) {
                scope.outReprocessedCount = data.length;
            });


            InboundInvalidFactory.size(function (data) {
                scope.inInvalidCount = data.length;
            });

            InboundMessageFactory.size(function (data) {
                scope.inAllCount = data.length;
            });
        });
    };
});