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
                exactSearch: false,
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
            },
            exact: function () {
                this.exactSearch = !this.exactSearch;
            }
        });
}

/* Controllers */

var app = angular.module('peppolApp.controllers', []);

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
                            if (!customer_list_enabled) {
                                for (var i = 0; i < data.length; i++) {
                                    data[i].$edit = false;
                                }
                            }
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

app.controller('MessageCtrl', ['$scope', '$resource', '$location', '$timeout', 'FailedFactory', 'InvalidFactory', 'MessageFactory', 'ReprocessFactory', '$window', '$route', '$modal', '$filter',
    function ($scope, $resource, $location, $timeout, FailedFactory, InvalidFactory, MessageFactory, ReprocessFactory, $window, $route, $modal, $filter) {
        $scope.showMessageInfo = function (msg) {
            msg.showMessageDesc = (msg.showMessageDesc != true);
            if (msg.showMessageDesc) {
                MessageFactory.get_details(msg.id, function (data) {
                    msg.files = data;
                });
            }
            msg.$selected = !msg.$selected;
        };
        $scope.showFileName = function (fileName, chars) {
            if (!Boolean(fileName))
                return '';
            var normalizedName = getSimpleName(fileName);
            return (normalizedName.length <= chars) ? normalizedName : normalizedName.substr(0, chars) + '...';
        };

        var getSimpleName = function (fileName) {
            if (!Boolean(fileName))
                return '';
            return fileName.split(/(\\|\/)/g).pop();
        };

        $scope.showSimpleName = function (fileName) {
            return getSimpleName(fileName);
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
            $window.open('/rest/outbound/download?file=' + filename);
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
            } else /*if ($location.path().indexOf("/outbound") === 0) */{
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
            return timestamp > comparingDate.getTime();
        };

        $scope.getPartyDataTitle = function () {
            if (location.pathname.includes("/inbound"))
                return "Sender";
            if (location.pathname.includes("/customer"))
                return "Partner";
            return "Recipient";
        };
        <!--invoiceDate-->
        $scope.today = function () {
            $scope.invoiceDate = new Date();
        };

        $scope.today();

        $scope.clear = function () {
            $scope.invoiceDate = null;
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };

        $scope.dateOptions = {
            dateDisabled: disabled,
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.toggleMin = function () {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };

        $scope.toggleMin();

        $scope.setDate = function (year, month, day) {
            $scope.invoiceDate = new Date(year, month, day);
        };


        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = 'yyyy-MM-dd';
        $scope.altInputFormats = ['yyyy/M!/d!'];

        $scope.popup = {
            opened: false
        };

        $scope.openDatePicker = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.popup.opened = true;
        };

        // Disable weekend selection
        function disabled(data) {
            var date = data.date,
                mode = data.mode;
            return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
        }

        var tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);

        var afterTomorrow = new Date();
        afterTomorrow.setDate(tomorrow.getDate() + 1);

        $scope.changeSelect = function (dt) {
            if (dt == undefined)
                return;
            var userDate = new Date();
            dt.setHours(userDate.getHours());
            dt.setMinutes(userDate.getMinutes());
            dt.setSeconds(userDate.getSeconds());
        };

        $scope.events = [
            {
                date: tomorrow,
                status: 'full'
            },
            {
                date: afterTomorrow,
                status: 'partially'
            }
        ];

        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0, 0, 0, 0);

                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0, 0, 0, 0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
            return '';
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

app.controller("LogoutCtrl", ["$scope", "$location", "$route", function ($scope, $location, $route) {
    console.log("logout controller");
    /* window.localStorage.clear();
     window.location = '/login?logout=true';*/
    //$route.reload();
}]);


app.controller('sideMenu', ['$scope', function ($scope) {
    $scope.active;
}]);