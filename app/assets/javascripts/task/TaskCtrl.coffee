
class TaskCtrl

  constructor: (@$log, @TaskService) ->
    @$log.debug "constructing TaskController"
    @tasks = []
#    @newTaskId=6
#    @newWorkerId=1
#    @newCheckEvery=2000
#    @newProcessTime=1500
    @getTasks()

  getTasks: () ->
    @$log.debug "getTasks()"

    @TaskService.listTasks()
    .then(
      (data) =>
        @$log.debug "Promise returned #{data.length} Task"
        @tasks = data
    ,
    (error) =>
      @$log.error "Unable to get Task: #{error}"
    )

  reset: () ->
    @$log.debug "reset()"

    @TaskService.reset()
    .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} Task"
          @getTasks()
      ,
      (error) =>
        @$log.error "Unable to get Task: #{error}"
      )

  addTask: (taskId) ->
    @$log.debug "add()"

    @TaskService.addTask(taskId)
    .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} Task"
          @getTasks()
      ,
      (error) =>
        @$log.error "Unable to get Task: #{error}"
      )

  startWorker: (worker,every,processTime) ->
    @$log.debug "start()"

    @TaskService.startWorker(worker,every,processTime)
    .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} Task"
          @getTasks()
      ,
      (error) =>
        @$log.error "Unable to get Task: #{error}"
      )

controllersModule.controller('TaskCtrl', TaskCtrl)