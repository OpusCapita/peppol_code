'use strict';

angular.module('peppolApp', [
    'ngRoute',
    'peppolApp.services',
    'peppolApp.controllers',
    'ngTable',
    'ngSanitize',
    'ngAnimate',
    'ui.bootstrap'
])
    .config(function ($routeProvider, $httpProvider, $locationProvider, $logProvider) {
        $routeProvider.when('/customers', {
            templateUrl: '/views/customer-list.html',
            controller: 'CustomerCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/access_point', {
            templateUrl: '/views/access_point-list.html',
            controller: 'AccessPointCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/outbound', {
            templateUrl: '/views/message-list.html',
            controller: 'AllMessageCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/customer/:cust_id/messages', {
            templateUrl: '/views/message-list.html',
            controller: 'CustomerMessageCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/outbound/failed', {
            templateUrl: '/views/message-list.html',
            controller: 'FailedCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/outbound/invalid', {
            templateUrl: '/views/message-list.html',
            controller: 'InvalidCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/outbound/processing', {
            templateUrl: '/views/message-list.html',
            controller: 'ProcessingCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/outbound/reprocessed', {
            templateUrl: '/views/message-list.html',
            controller: 'ReprocessedCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/inbound', {
            templateUrl: '/views/message-list.html',
            controller: 'InboundMessageCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/inbound/invalid', {
            templateUrl: '/views/message-list.html',
            controller: 'InvalidInboundMessageCtrl',
            reloadOnSearch: false
        });
        $routeProvider.when('/status', {templateUrl: '/views/status.html', controller: 'StatusCtrl'});
        $routeProvider.when('/signout', {templateUrl: '/views/status.html', controller: 'LogoutCtrl'});
        $routeProvider.otherwise({redirectTo: '/customers'});

        /* CORS... */
        /* http://stackoverflow.com/questions/17289195/angularjs-post-data-to-external-rest-api */
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];

        $logProvider.debugEnabled(false);

        $locationProvider.html5Mode(true);
        $locationProvider.hashPrefix('!');
    });

$(document).ready(function () {
    $('.tree-toggle').click(function () {
        $(this).parent().children('ul.tree').toggle(200);
    });
});
