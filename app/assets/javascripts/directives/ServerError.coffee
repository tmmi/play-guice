angular.module 'myApp.directives'
.directive 'serverError', ->
    restrict: 'EA'
    controller: ($scope, $rootScope, $timeout) ->
      $scope.hasErrors = false
      $rootScope.$on 'error.generic', (error, args) ->
        $scope.showErrorMessage "Error #{error.name}.#{args.code}"

      $scope.hideErrorMessage = ->
        $scope.hasErrors = false
        $scope.message = ''

      $scope.showErrorMessage = (message)->
        $scope.hasErrors = true
        $scope.message = message
        $timeout $scope.hideErrorMessage, 3000

    link: (scope, element) ->
      scope.$watch 'hasErrors', (hasError)-> if hasError then element.addClass 'show-error' else element.removeClass 'show-error'

    replace: true
    template: """
          <div class="generic-error-container bg-danger">
          <div class="error-message">{{message}}<i class="glyphicon glyphicon-remove" ng-click="hideErrorMessage()"></i>
          </div>
          </div>
    """