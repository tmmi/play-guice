
class ConfigCtrl

  constructor: (@$log, @ConfigService) ->
    @$log.debug "constructing ConfigController"
    @configs = []
    @getConfigs()

  getConfigs: () ->
    @$log.debug "getConfig()"

    @ConfigService.listConfigs()
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length} configs"
        @configs = data
    ,
    (error) =>
      @$log.error "Unable to get Config: #{error}"
    )

  setConfig: (key, value) ->
    @$log.debug "setConfig()"

    @ConfigService.setConfig(key,value)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length}"
        @getConfigs()
    ,
    (error) =>
      @$log.error "Unable to set Config: #{error}"
    )

  removeConfig: (key) ->
    @$log.debug "removeConfig()"

    @ConfigService.removeConfig(key)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length}"
        @getConfigs()
    ,
    (error) =>
      @$log.error "Unable to set Config: #{error}"
    )

controllersModule.controller('ConfigCtrl', ConfigCtrl)