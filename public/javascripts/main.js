$(function() {
    ws = null

    ws = new WebSocket("ws://localhost:9000/tasks/notifications")
    ws.onmessage = function(msg) {
        //$('<li />').text(msg.data).appendTo('#messages')

        var taskUpdate = angular.fromJson(msg.data)[0];
        var i;
        var scope = angular.element(document.querySelector('#tasks')).scope();
        scope.$apply(function() {
            for(i=0; i< scope.tc.tasks.length; i++) {
                stask = scope.tc.tasks[i];
                if ( stask.taskId === taskUpdate.taskId) {
                    stask.worker = taskUpdate.worker;
                    stask.status = taskUpdate.status;
                    stask.retry = taskUpdate.retry;
                    break;
                }
            }
        });

    }
})