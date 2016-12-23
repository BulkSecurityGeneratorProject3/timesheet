(function() {
    'use strict';

    angular
        .module('timesheetApp')
        .controller('TimesheetDeleteController',TimesheetDeleteController);

    TimesheetDeleteController.$inject = ['$uibModalInstance', 'entity', 'Timesheet'];

    function TimesheetDeleteController($uibModalInstance, entity, Timesheet) {
        var vm = this;

        vm.timesheet = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Timesheet.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
