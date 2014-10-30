
class ConfigService

  @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
  @defaultConfig = { headers: @headers }

  constructor: (@$log, @$http, @$q) ->
    @$log.debug "constructing ConfigService"

  listConfigs: () ->
    @$log.debug "listConfigs()"
    deferred = @$q.defer()

    @$http.get("/list")
    .success((data, status, headers) =>
      @$log.info("Successfully listed Configs - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to list Configs - status #{status}")
      deferred.reject(data);
    )
    deferred.promise

  setConfig: (key, value) ->
    @$log.debug "setConfig #{key} #{value}"
    deferred = @$q.defer()

    @$http.get("/set/#{key}/#{value}")
    .success((data, status, headers) =>
      @$log.info("Successfully updated config - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to updated config - status #{status}")
      deferred.reject(data);
    )
    deferred.promise

  removeConfig: (key) ->
    @$log.debug "removeConfig #{key}"
    deferred = @$q.defer()

    @$http.get("/remove/#{key}")
    .success((data, status, headers) =>
      @$log.info("Successfully remove config - status #{status}")
      deferred.resolve(data)
    )
    .error((data, status, headers) =>
      @$log.error("Failed to remove config - status #{status}")
      deferred.reject(data);
    )
    deferred.promise

#{angular.toJson(user, true)}

servicesModule.service('ConfigService', ConfigService)