(function() {
    'use strict';

    angular
        .module('timesheetApp')
        .controller('TimesheetDetailController', TimesheetDetailController);

    TimesheetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Timesheet'];

    function TimesheetDetailController($scope, $rootScope, $stateParams, previousState, entity, Timesheet) {
        var vm = this;

        vm.timesheet = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('timesheetApp:timesheetUpdate', function(event, result) {
            vm.timesheet = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
