package controllers

import javax.inject.{Inject, Singleton}
import services.{Task, DBService}
import akka.actor._
import play.api.libs.json.{JsValue, Json}
import play.libs.Akka
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import services.Task
import scala.Some
import play.api.mvc.{Action, WebSocket}
import scala.concurrent.Future
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class TaskController  @Inject()(dbService: DBService) extends Controller{
  var supervisor:ActorRef = Akka.system.actorOf(Props[SuperVisorActor])

  /*
class Parent extends Actor {
  val c = context.actorOf(Props[Child], name = "child")
  c ! "hello"
  def receive = {
    case x: String ⇒ println(self.path.name + " : " + x)
  }
}

class Child extends Actor {
  def receive = {
    case x: String ⇒
      println(self.path.name + " : " + x)
      context.parent ! x.toUpperCase
  }
}

val system = ActorSystem().actorOf(Props[Parent], name = "parent")
 */


  def startWorker(workerId: Int, everyMillis: Long, processTime: Long) = Action {
    import play.api.libs.concurrent.Execution.Implicits._
    Akka.system.scheduler.schedule(Duration(500, TimeUnit.MILLISECONDS), Duration(everyMillis, TimeUnit.MILLISECONDS),
      Akka.system.actorOf(WorkerActor.props(processTime,dbService)(supervisor),"worker"+ workerId), Tick(workerId))

    Ok("started")
  }

  // Generates Writes and Reads for
  implicit val taskFormat = Json.format[Task]

  def tasks = Action {
    Ok(views.html.tasks())
  }

  def allTasks() = Action {
    Ok(Json.toJson(dbService.getAllTasks))
  }

  def taskNotifications = WebSocket.tryAcceptWithActor[JsValue, JsValue] { implicit request =>
    Future.successful(Right(MonitorActor.props(dbService,supervisor)))
  }

  def addTask(id: Long) = Action {
    dbService.addTask(id)
    Ok("ok "+ id)
  }

  def reset = Action {
    dbService.resetRetry
    Ok("Reseted")
  }

  /** **/
}


case class Tick(workerId: Int)
case class Finish(workerId: Int, taskId: Long)
case class Changed(workerId: Int, taskId: Long)
case class Subscribe(monitor: ActorRef)

class WorkerActor(jobTime: Long, dbService:DBService, out: ActorRef) extends Actor with ActorLogging {
  // Generates Writes and Reads for
  implicit val taskFormat = Json.format[Task]

  override def preStart() = {
    //self ! Tick(1)
  }

  private var countNoJob = 0

  def receive = {
    case Tick(workerId) => {
      println(workerId + " start")
      val res = dbService.getNextToPerform(workerId)

      res match {
        case Left(id) => {
          println(workerId + " Id to process:" + id)
          out ! Changed(workerId, id)
          //update(id,status=1,worker?)
          countNoJob = 0
          Akka.system.scheduler.scheduleOnce(Duration(jobTime, TimeUnit.MILLISECONDS), self, Finish(workerId, id))
        }
        case Right(i) => {
          println("No new: " + i)
          countNoJob = countNoJob + 1
          if (workerId!=0 && countNoJob > 5) self ! PoisonPill
        }
      }
    }
    case Finish(workerId, id) => {
      println(workerId + " finished " + id)
      dbService.updateStatus(id, 0)
      //self ! Tick(workerId)
      out ! Changed(workerId, id)
    }
  }
}


class SuperVisorActor extends Actor with ActorLogging {

  var out: Option[ActorRef] = None

  def receive = {
    case Subscribe(actor) => out = Some(actor)
    case Changed(workerId, id) => {
      if( out.isDefined) {
        out.get ! Finish(workerId, id)
      }
    }
  }
}

class MonitorActor(dbService:DBService,supervisor:ActorRef, out: ActorRef) extends Actor with ActorLogging {
  // Generates Writes and Reads for
  implicit val taskFormat = Json.format[Task]

  override def preStart() = {
    supervisor ! Subscribe(self)
  }

  def receive = {
    case Finish(workerId, id) => {
      out ! Json.toJson(dbService.getTaskById(id))
    }
  }
}


object WorkerActor {
  def props(jobTime: Long,dbService:DBService)(out: ActorRef) = Props(new WorkerActor(jobTime, dbService, out))
}


object MonitorActor {
  def props(dbService:DBService,supervisor: ActorRef)(out: ActorRef) = Props(new MonitorActor( dbService,supervisor, out))
}
