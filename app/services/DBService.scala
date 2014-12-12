package services

import anorm.SqlParser._
import anorm._
import org.slf4j.{Logger, LoggerFactory}
import play.api.db.DB


case class Task(taskId:Long,worker:Long,status:Long,retry:Long)

class DBService {
  import play.api.Play.current

  private final val logger: Logger = LoggerFactory.getLogger(classOf[DBService])

  def updateStatus(taskId:Long, status:Byte) = {
    DB.withConnection { implicit c =>
      SQL(DBService.updateSql(taskId, 0, status)).executeUpdate
    }
  }

  def resetRetry = {
    DB.withConnection { implicit c =>
      SQL(DBService.resetRetry).executeUpdate
    }
  }

  def addTask(taskId:Long) = {
    DB.withConnection { implicit c =>
      SQL(DBService.insertSql(taskId, "", 0, 0)).executeUpdate
    }
  }

  def getAllTasks = {
    getFindTask(DBService.selectAll)
  }

  def getTaskById(taskId:Long) = {
    getFindTask(DBService.selectByTaskId(taskId))
  }

  def getFindTask(query:String) = {
    DB.withConnection { implicit c =>
      val sql: SqlQuery = SQL(query)

      sql.as(get[java.math.BigDecimal]("TASK_ID") ~ get[java.math.BigDecimal]("STATUS") ~ get[java.math.BigDecimal]("WORKER_ID") ~ get[java.math.BigDecimal]("RETRY") *).map {
        case taskId ~ status ~ worker ~ retry => Task(taskId.longValue,worker.longValue, status.longValue, retry.longValue)
      }
    }
  }

  def getNextToPerform(worker: Int): Either[Long, Boolean] = {
    DB.withConnection { implicit c =>
      try {
        val sql: SqlQuery = SQL(DBService.selectFirstId(5))

        val ids = sql.map(row => row[BigDecimal]("task_Id")).list().map( _.longValue)

        if (ids.length > 0) {
          val id = ids(0)

          if (SQL(DBService.updateSql(id, worker, 1)).executeUpdate() > 0) {
            Left(id)
          } else {
            logger.error(s"$worker Already selected $id")
            Right(false)
          }
        }
        else {
          logger.error(s"$worker Nothing to select")
          Right(false)
        }
      } catch {
        case e: Exception => {
          logger.error(s"$worker exception caught: $e")
          Right(true)
        };
      }
    }
  }
}


object DBService {
  val tableName="TASK_LIST"
  val createSQL = s"""
          CREATE TABLE $tableName
          (
           TASK_ID NUMBER PRIMARY KEY,
           TYPE VARCHAR2(10) DEFAULT 'any' ,
           PRIORITY NUMBER DEFAULT 0,
           INFOTXT VARCHAR2(256),
           WORKER_ID NUMBER,
           PROCESS_DATE DATE,
           RETRY NUMBER,
           CREATE_DATE DATE,
           STATUS NUMBER,
           REASON VARCHAR2(4000)
          )
                  """

  def insertSql(taskId: Long, txt: String, workerId: Long, status: Byte) =
    s"insert into $tableName values ($taskId,'any',0, '$txt', $workerId, null,0 , NOW(), $status, '')"

  def updateSql(taskId: Long, workerId: Long, status: Byte) =
   if(status == 0)
    s"update $tableName SET worker_Id=$workerId, status=$status, RETRY=RETRY+1, PROCESS_DATE=null where task_id=$taskId"
   else
    s"update $tableName SET worker_Id=$workerId, status=$status, PROCESS_DATE=NOW() where task_id=$taskId"

  def selectFirstId(limitRetry: Long = 5) =
    s"select task_id from $tableName where status=0 and RETRY<$limitRetry ORDER BY PRIORITY DESC LIMIT 1"

  def selectAll =
    s"select * from $tableName"

  def selectByTaskId(taskId: Long) =
    s"$selectAll where task_Id=$taskId"

  def resetRetry=
    s"update $tableName SET retry=0 where RETRY>0 and status=0"

}
