
class TaskService

  @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
  @defaultConfig = { headers: @headers }

  constructor: (@$log, @$http, @$q) ->
    @$log.debug "constructing TaskService"

  listTasks: () ->
    @$log.debug "listTasks()"
    deferred = @$q.defer()

    @$http.get("/tasks.json")
    .success((data, status, headers) =>
      @$log.info("Successfully listed tasks - status #{status}")
      deferred.resolve(data)
    )
    deferred.promise

  reset: () ->
    @$log.debug "reset()"
    deferred = @$q.defer()

    @$http.get("/reset")
    .success((data, status, headers) =>
        @$log.info("Successfully reset #{status}")
        deferred.resolve(data)
      )
    deferred.promise

  startWorker: (worker, every, processTime) ->
    @$log.debug "start #{worker}"
    deferred = @$q.defer()
    @$http.get("/start/#{worker}/#{every}/#{processTime}")
    .success((data, status, headers) =>
      @$log.info("Successfully start - status #{status}")
      deferred.resolve(data)
    )
    deferred.promise

  addTask: (taskId) ->
    @$log.debug "addTask #{taskId}"
    deferred = @$q.defer()

    @$http.get("/add/#{taskId}")
    .success((data, status, headers) =>
      @$log.info("Successfully add task - status #{status}")
      deferred.resolve(data)
    )
    deferred.promise

#{angular.toJson(user, true)}

servicesModule.service('TaskService', TaskService)