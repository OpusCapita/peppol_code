'use strict';

var services = angular.module('peppolApp.services', ['ngResource']);

if (!window.location.origin)
    window.location.origin = window.location.protocol + "//" + window.location.host;
var baseUrl = window.location.origin + "/" + window.location.pathname.split("/")[1];


// Customer Factories
services.factory('CustomerFactory', function ($resource) {
    var url = "/rest/customers";
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'},
        update: {method: 'PUT', params: {id: '@id'}}
    })
});

// AP Factories
services.factory('AccessPointFactory', function ($resource) {
    var url = "/rest/access_point";
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'},
        update: {method: 'PUT', params: {id: '@id'}}
    })
});
// Message Factories
services.factory('MessageFactory', function ($resource, $cacheFactory) {
    var cache = $cacheFactory('MessageFactory');
    var url = '/rest/outbound';
    return getAllMessageFactory($resource, url, cache);
    /*var Message = $resource(url, {}, {
     query: { method: 'GET', isArray: true},
     size: { method: 'GET', url: url + '/count'},
     get_details: { method: 'GET', url: url + '/details/:msg_id', isArray: true}
     });
     return {
     query: function (query, callback) {
     Message.query(query, callback);
     },
     size: function (callback) {
     Message.size(callback);
     },
     get_details: function (msg_id, callback) {
     var details = cache.get(msg_id);
     if (!details) {
     details = Message.get_details({msg_id: msg_id});
     cache.put(msg_id, details);
     }
     details.$promise.then(callback);
     }
     }*/
});

services.factory("InboundMessageFactory", function ($resource, $cacheFactory) {
    var cache = $cacheFactory('InboundMessageFactory');
    var url = '/rest/preprocessing';
    return getAllMessageFactory($resource, url, cache);
});

var getAllMessageFactory = function ($resource, url, cache) {
    var Message = $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'},
        get_details: {method: 'GET', url: url + '/details/:msg_id', isArray: true}
    });
    return {
        query: function (query, callback) {
            Message.query(query, callback);
        },
        size: function (callback) {
            Message.size(callback);
        },
        get_details: function (msg_id, callback) {
            var details = cache.get(msg_id);
            if (!details) {
                details = Message.get_details({msg_id: msg_id});
                cache.put(msg_id, details);
            }
            details.$promise.then(callback);
        }
    }
};

services.factory('FailedFactory', function ($resource) {
    var url = '/rest/outbound/failed';
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'},
        get_error: {method: 'GET', url: '/rest/outbound/get_error'},
        reprocess: {method: 'POST', url: url + '/reprocess/:file_id', params: {file_id: '@file_id'}},
        resolve_manually: {
            method: 'POST',
            url: url + '/resolve_manually/:msg_id',
            params: {msg_id: '@msg_id', comment: '@comment'}
        }
    })
});

services.factory('ReprocessFactory', function ($resource) {
    var url = '/rest/reprocess';
    return $resource(url, {}, {
        reprocess_inbound: {method: 'POST', url: url + '/preprocessing/:file_ids', params: {file_ids: '@file_ids'}},
        reprocess_outbound: {method: 'POST', url: url + '/outbound/:file_ids', params: {file_ids: '@file_ids'}}
    })
});

services.factory('InvalidFactory', function ($resource) {
    var url = '/rest/outbound/invalid';
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'},
        get_error: {method: 'GET', url: '/rest/outbound/get_error'}
    })
});

services.factory('InboundInvalidFactory', function ($resource) {
    var url = '/rest/preprocessing/invalid';
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'},
        get_error: {method: 'GET', url: '/rest/outbound/get_error'}
    })
});

services.factory('ProcessingFactory', function ($resource) {
    var url = '/rest/outbound/processing';
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'}
    })
});
services.factory('ReprocessedFactory', function ($resource) {
    var url = '/rest/outbound/reprocessed';
    return $resource(url, {}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count'}
    })
});


services.factory('CustomerMessageFactory', function ($resource) {
    var url = '/rest/outbound/customer/:cust_id';
    return $resource(url, {cust_id: '@cust_id'}, {
        query: {method: 'GET', isArray: true},
        size: {method: 'GET', url: url + '/count', params: {cust_id: '@cust_id'}}
    })
});


// Status Factory
services.factory('StatusFactory', function ($resource) {
    var url = '/rest/status';
    return $resource(url, {}, {
        amqp: {method: 'GET', url: url + '/amqp', isArray: true},
        quartz: {method: 'GET', url: url + '/quartz', isArray: false}
    })
});