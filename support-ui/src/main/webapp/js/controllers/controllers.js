'use strict';
var DEFAULT_COLSPAN = 9;

function escapeParams(params) {
    /*for (var i in params) {
     params[i] = params[i].replace('%25', '');
     }*/
    return params;
}

function createTable(ngTableParams, $location, $timeout, Factory, $scope, inbound) {
    return new ngTableParams(
        angular.extend({
                page: 1,            // show first page
                count: 50,          // count per page
                sorting: {
                    'id': 'desc'
                },
                search: $scope.searchCriteria
            },
            $location.search()),
        {
            groupBy: function (item) {
                return (inbound ? 'Recipient ' : 'Sender: ') + item.senderIdentifier + ' (' + item.senderName + ')';
            },           // length of data
            getData: function ($defer, params) {
                $location.search(escapeParams(params.url()));
                Factory.size(function (data) {
                    params.total(data.length);
                });
                // ajax request to api
                Factory.query(params.url(), function (data) {
                    $timeout(function () {
                        // set new data
                        $scope.messages = data;
                        $scope.messageList = {};
                        angular.forEach($scope.messages, function (item) {
                            $scope.messageList[item.id] = item;
                        });
                        $defer.resolve(data);
                    }, 200);
                });
            },
            search: function (data) {
                this.data = data;
                this.settings.$scope.pages = self.generatePagesArray(self.page(), self.total(), self.count());
            }
        });
}

/* Controllers */

var app = angular.module('peppolApp.controllers', []);

// Clear browser cache (in development mode)
//
// http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
app.run(function ($rootScope, $templateCache) {
    $rootScope.$on('$viewContentLoaded', function () {
        $templateCache.removeAll();
    });
});


app.controller('CustomerCtrl', ['$scope', 'CustomerFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, CustomerFactory, $resource, $location, ngTableParams, $timeout) {
        $scope.tableParams = new ngTableParams(
            angular.extend({
                    page: 1,            // show first page
                    count: 50          // count per page
                },
                $location.search()),
            {
                getData: function ($defer, params) {
                    $location.search(escapeParams(params.url()));
                    CustomerFactory.size(function (data) {
                        params.total(data.length);
                    });
                    // ajax request to api
                    CustomerFactory.query(params.url(), function (data) {
                        $timeout(function () {
                            // set new data
                            $defer.resolve(data);
                        }, 200);
                    });
                }
            });

        var backupCustomers = {};
        $scope.alerts = [];
        $scope.updateCustomer = function (customer) {
            backupCustomers[customer.id] = angular.copy(customer);
            //backupCustomers[customer.id] = customer;
            customer.$edit = true
        };
        $scope.reverseChanges = function (customer) {
            for (var key in backupCustomers[customer.id]) {
                if (backupCustomers[customer.id].hasOwnProperty(key)) {
                    customer[key] = backupCustomers[customer.id][key];
                }
            }
            customer.$edit = false;
        };
        $scope.saveCustomerChanges = function (customer) {
            CustomerFactory.update(customer, function (data) {
                $scope.alerts = [
                    {type: "success", msg: "Customer " + customer.name + " successfully updated."}
                ];
                customer.$edit = false;
            }, function (error) {
                $scope.alerts = [
                    {type: "danger", msg: "Failed to update customer " + customer.name}
                ];
            });
        };
        $scope.closeAlert = function (idx) {
            $scope.alerts = [];
        };
        $scope.showEmails = function (emails) {
            if (emails != undefined || emails !== null)
                return emails.split(",").join(" ");
        };
    }]);

app.controller('AccessPointCtrl', ['$scope', 'AccessPointFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, AccessPointFactory, $resource, $location, ngTableParams, $timeout) {
        $scope.tableParams = new ngTableParams(
            angular.extend({
                    page: 1,            // show first page
                    count: 50          // count per page
                },
                $location.search()),
            {
                getData: function ($defer, params) {
                    $location.search(escapeParams(params.url()));
                    AccessPointFactory.size(function (data) {
                        params.total(data.length);
                    });
                    // ajax request to api
                    AccessPointFactory.query(params.url(), function (data) {
                        $timeout(function () {
                            // set new data
                            $defer.resolve(data);
                        }, 200);
                    });
                }
            });

        var backup = {};
        $scope.alerts = [];
        $scope.updateAccessPoint = function (accessPoint) {
            backup[accessPoint.id] = angular.copy(accessPoint);
            //backupCustomers[customer.id] = customer;
            accessPoint.$edit = true
        };
        $scope.reverseChanges = function (accessPoint) {
            for (var key in backup[accessPoint.id]) {
                if (backup[accessPoint.id].hasOwnProperty(key)) {
                    accessPoint[key] = backup[accessPoint.id][key];
                }
            }
            accessPoint.$edit = false;
        };
        $scope.saveAccessPointChanges = function (accessPoint) {
            AccessPointFactory.update(accessPoint, function (data) {
                $scope.alerts = [
                    {type: "success", msg: "Access Point " + accessPoint.accessPointId + " successfully updated."}
                ];
                accessPoint.$edit = false;
            }, function (error) {
                $scope.alerts = [
                    {type: "danger", msg: "Failed to update Access Point " + accessPoint.accessPointId}
                ];
            });
        };
        $scope.closeAlert = function (idx) {
            $scope.alerts = [];
        };
        $scope.showEmails = function (emails) {
            if (emails != undefined || emails !== null)
                return emails.split(",").join(" ");
        };
    }]);

app.controller('MessageCtrl', ['$scope', '$resource', '$location', '$timeout', 'FailedFactory', 'InvalidFactory', 'MessageFactory', 'ReprocessFactory', '$window', '$route', '$modal',
    function ($scope, $resource, $location, $timeout, FailedFactory, InvalidFactory, MessageFactory, ReprocessFactory, $window, $route, $modal) {
        $scope.showMessageInfo = function (msg) {
            msg.showMessageDesc = (msg.showMessageDesc != true);
            if (msg.showMessageDesc) {
                MessageFactory.get_details(msg.id, function (data) {
                    msg.files = data;
                });
            }
            msg.$selected = !msg.$selected;
        };
        $scope.showFullError = function (failed) {
            if (typeof failed.fullError == 'undefined') {
                var Factory = failed.invalid ? InvalidFactory : FailedFactory;
                Factory.get_error({'file': failed.errorFilePath}, function (data) {
                    $timeout(function () {
                        // set new data
                        if (data.fullError == "") {
                            data.fullError = "Not available!"
                        }
                        angular.extend(failed, data);
                        failed.$showDescr = (failed.$showDescr != true);
                    }, 200);

                }, function (error) {
                    angular.extend(failed, {fullError: "Not available!"});
                });
            } else {
                failed.$showDescr = (failed.$showDescr != true);
            }
        };
        $scope.downloadFile = function (filename) {
            $window.open('/rest/outbound/download/' + filename);
        };

        $scope.isAllowedToReprocess = function () {
            var allow = true;

            angular.forEach($scope.checkboxes.items, function (item, key) {
                if ($scope.messageList != undefined && item) {
                    allow = false;
                }
            });
            return allow;
        };

        var reprocessMessages = function (fileIds) {
            if ($location.path().indexOf("/inbound") === 0) {
                ReprocessFactory.reprocess_inbound({file_ids: fileIds}, function () {
                    $timeout(function () {
                        $route.reload();
                    }, 300);
                });
            } else {
                ReprocessFactory.reprocess_outbound({file_ids: fileIds}, function () {
                    $timeout(function () {
                        $route.reload();
                    }, 300);
                });
            }
        };

        $scope.reprocessFile = function (file, msg) {
            if (confirm("Reprocess invoice: " + msg.invoiceNumber + " (" + file.filename + ") ?")) {
                reprocessMessages(file.id);
            }
        };

        $scope.reprocessSelectedMessages = function () {
            var messageList = getSelectedMessages();
            if (confirm("Reprocess all selected (" + messageList.length + ") invoices?")) {
                var fileIds = [];
                angular.forEach(messageList, function (message) {
                    fileIds.push(message.fileId);
                });
                reprocessMessages(fileIds);
            }
        };

        $scope.openResolveModal = function (message) {
            createResolveModal([message], function (comment) {
                $scope.resolveMessage(message, comment);
            });
        };
        $scope.resolveMessage = function (msg, comment) {
            FailedFactory.resolve_manually({msg_id: msg.id, comment: comment}, function () {
                $timeout(function () {
                    $route.reload();
                }, 300);
            })
        };

        var createResolveModal = function (messages, callback) {
            var modalInstance = $modal.open({
                templateUrl: '/views/resolve-modal.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    messages: function () {
                        return messages;
                    }
                }
            });

            modalInstance.result.then(function (comment) {
                callback(comment);
            });
        };

        // Checkbox impl
        $scope.checkboxes = {'checked': false, items: {}};
        $scope.$watch('checkboxes.checked', function (value) {
            angular.forEach($scope.messages, function (item) {
                if (angular.isDefined(item.id)) {
                    $scope.checkboxes.items[item.id] = value;
                }
            });
        });

        // watch for data checkboxes
        $scope.$watch('checkboxes.items', function (values) {
            if (!$scope.messages) {
                return;
            }
            var checked = 0, unchecked = 0,
                total = $scope.messages.length;
            angular.forEach($scope.messages, function (item) {
                checked += ($scope.checkboxes.items[item.id]) || 0;
                unchecked += (!$scope.checkboxes.items[item.id]) || 0;
            });
            if ((unchecked == 0) || (checked == 0)) {
                $scope.checkboxes.checked = (checked == total);
            }
            // grayed checkbox
            angular.element(document.getElementById("select_all")).prop("indeterminate", (checked != 0 && unchecked != 0));
        }, true);

        /*$scope.isAllowedToReprocess = function () {
         var hasFailedMessages = false,
         notOnlyErrors = true;

         angular.forEach($scope.checkboxes.items, function (item, key) {
         if ($scope.messageList != undefined && item && ($scope.messageList[key].status == "failed" || $scope.messageList[key].status == "processing")) {
         hasFailedMessages = true;
         } else if ($scope.messageList != undefined && item) {
         notOnlyErrors = false;
         }
         });
         return !(hasFailedMessages && notOnlyErrors);
         };*/


        $scope.isAllowedToResolve = function () {
            var hasErrorMessages = false,
                notOnlyErrors = true;
            angular.forEach($scope.checkboxes.items, function (item, key) {
                if ($scope.messageList != undefined && item &&
                    ($scope.messageList[key].status == "failed" || $scope.messageList[key].status == "invalid" ||
                    $scope.messageList[key].status == "reprocessing" || $scope.messageList[key].status == "processing")) {
                    hasErrorMessages = true;
                } else if ($scope.messageList != undefined && item) {
                    notOnlyErrors = false;
                }
            });
            return !(hasErrorMessages && notOnlyErrors);
        };


        $scope.resolveSelectedMessages = function () {
            var messageList = getSelectedMessages();
            createResolveModal(messageList, function (comment) {
                angular.forEach(messageList, function (message) {
                    $scope.resolveMessage(message, comment);
                });
            });
        };

        var getSelectedMessages = function () {
            var messageList = [];
            angular.forEach($scope.checkboxes.items, function (value, key) {
                if ($scope.messageList != undefined && value) {
                    this.push($scope.messageList[key]);
                }
            }, messageList);
            return messageList;
        };
        $scope.isAllowedToDownload = function (timestamp) {
            var comparingDate = new Date();
            comparingDate.setMonth(comparingDate.getMonth() - 3);
            console.log("Timestamp diff: " + String(comparingDate.getTime() - timestamp));
            return timestamp > comparingDate.getTime();
        };

        $scope.getRecipientDataTitle = function () {
            return location.pathname.includes("/inbound") ? "Sender" : "Recipient";
        };
    }]);

var ModalInstanceCtrl = ['$scope', '$modalInstance', 'messages', function ($scope, $modalInstance, messages) {
    $scope.messages = messages;
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
    $scope.ok = function (isValid, resolvedComment) {
        if (isValid) {
            $modalInstance.close(resolvedComment);
        } else {
            alert("Comment is required!")
        }
    };
}];

function MessageCtrlHandler($scope, MessageFactory, $resource, $location, ngTableParams, $timeout, inbound) {
    $scope.colspan = DEFAULT_COLSPAN;
    $scope.tableParams = createTable(ngTableParams, $location, $timeout, MessageFactory, $scope, inbound);
}

app.controller('AllMessageCtrl', ['$scope', 'MessageFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, MessageFactory, $resource, $location, ngTableParams, $timeout) {
        MessageCtrlHandler($scope, MessageFactory, $resource, $location, ngTableParams, $timeout);
    }]);

app.controller('InboundMessageCtrl', ['$scope', 'InboundMessageFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, MessageFactory, $resource, $location, ngTableParams, $timeout) {
        MessageCtrlHandler($scope, MessageFactory, $resource, $location, ngTableParams, $timeout, true);
    }]);

app.controller('CustomerMessageCtrl', ['$scope', 'CustomerMessageFactory', '$resource', '$location', 'ngTableParams', '$timeout', '$routeParams',
    function ($scope, CustomerMessageFactory, $resource, $location, ngTableParams, $timeout, $routeParams) {
        $scope.colspan = DEFAULT_COLSPAN;
        $scope.tableParams = new ngTableParams(
            angular.extend({
                    page: 1,            // show first page
                    count: 50,          // count per page
                    sorting: {
                        'id': 'desc'
                    },
                    search: ""
                },
                $location.search()),
            {
                groupBy: function (item) {
                    return 'Sender: ' + item.senderName;
                },
                getData: function ($defer, params) {
                    $location.search(escapeParams(params.url()));
                    var customer = {'cust_id': $routeParams.cust_id};
                    CustomerMessageFactory.size(customer, function (data) {
                        params.total(data.length);
                    });
                    var url = params.url();
                    angular.extend(url, customer);
                    CustomerMessageFactory.query(url, function (data) {
                        $timeout(function () {
                            // set new data
                            $defer.resolve(data);
                        }, 200);
                    });
                }
            });
    }]);

// Failed and Invalid message controllers
function ErrorCtrlHandler($scope, Factory, $resource, $location, ngTableParams, $timeout, inbound) {
    $scope.hasErrors = true;
    $scope.colspan = DEFAULT_COLSPAN;
    $scope.tableParams = createTable(ngTableParams, $location, $timeout, Factory, $scope, inbound)
}

app.controller('FailedCtrl', ['$scope', 'FailedFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, FailedFactory, $resource, $location, ngTableParams, $timeout) {
        ErrorCtrlHandler($scope, FailedFactory, $resource, $location, ngTableParams, $timeout);
    }]);

app.controller('InvalidCtrl', ['$scope', 'InvalidFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, InvalidFactory, $resource, $location, ngTableParams, $timeout) {
        ErrorCtrlHandler($scope, InvalidFactory, $resource, $location, ngTableParams, $timeout);
    }]);

app.controller('InvalidInboundMessageCtrl', ['$scope', 'InboundInvalidFactory', '$resource', '$location', 'ngTableParams', '$timeout',
    function ($scope, InvalidFactory, $resource, $location, ngTableParams, $timeout) {
        ErrorCtrlHandler($scope, InvalidFactory, $resource, $location, ngTableParams, $timeout, true);
    }]);

app.controller("ProcessingCtrl", ["$scope", "ProcessingFactory", "$resource", "$location", "ngTableParams", "$timeout",
    function ($scope, ProcessingFactory, $resource, $location, ngTableParams, $timeout) {
        $scope.colspan = DEFAULT_COLSPAN;
        $scope.tableParams = createTable(ngTableParams, $location, $timeout, ProcessingFactory, $scope);
    }]);
app.controller("ReprocessedCtrl", ["$scope", "ReprocessedFactory", "$resource", "$location", "ngTableParams", "$timeout",
    function ($scope, ReprocessedFactory, $resource, $location, ngTableParams, $timeout) {
        $scope.colspan = DEFAULT_COLSPAN;
        $scope.tableParams = createTable(ngTableParams, $location, $timeout, ReprocessedFactory, $scope);
    }]);


app.controller('StatusCtrl', ['$scope', 'StatusFactory', '$location', function ($scope, StatusFactory, $location) {
    $scope.queues = StatusFactory.amqp();
    StatusFactory.quartz(function (result) {
        $scope.quartz = result;
        $scope.quartz.status = $scope.quartz.status.split("<br>").join("\n");
    });
}]);

app.controller("LogoutCtrl", ["$resource", "$route", "$location", function ($resource, $route, $location) {
    $resource('/logout', {}, {
        get: {method: 'GET'}
    }).get({}, function (data) {
        $location.path("/");
        $route.reload();
    })
}]);

app.controller('sideMenu', ['$scope', function ($scope) {
    $scope.active;
}]);