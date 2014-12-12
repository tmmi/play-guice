angular.module 'myApp.interceptors', []
.config ($provide, $httpProvider) ->
    $provide.factory 'errorHttpResponseInterceptor', ($q, $rootScope, $log) ->
      responseError: (rejection) ->
        $rootScope.$broadcast 'error.generic', code: rejection.status
        $log.warn 'Server: '+ rejection.config.url + " errorCode:" +  rejection.status

        $q.reject rejection

    $httpProvider.interceptors.push 'errorHttpResponseInterceptor'
