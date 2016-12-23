(function() {
    'use strict';

    angular
        .module('timesheetApp')
        .controller('TimesheetDialogController', TimesheetDialogController);

    TimesheetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Timesheet'];

    function TimesheetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Timesheet) {
        var vm = this;

        vm.timesheet = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.timesheet.id !== null) {
                Timesheet.update(vm.timesheet, onSaveSuccess, onSaveError);
            } else {
                Timesheet.save(vm.timesheet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timesheetApp:timesheetUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;
        vm.datePickerOpenStatus.timeIn = false;
        vm.datePickerOpenStatus.timeOut = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
