(function() {
    'use strict';
    angular
        .module('timesheetApp')
        .factory('Timesheet', Timesheet);

    Timesheet.$inject = ['$resource', 'DateUtils'];

    function Timesheet ($resource, DateUtils) {
        var resourceUrl =  'api/timesheets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date = DateUtils.convertLocalDateFromServer(data.date);
                        data.timeIn = DateUtils.convertDateTimeFromServer(data.timeIn);
                        data.timeOut = DateUtils.convertDateTimeFromServer(data.timeOut);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.date = DateUtils.convertLocalDateToServer(copy.date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.date = DateUtils.convertLocalDateToServer(copy.date);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
