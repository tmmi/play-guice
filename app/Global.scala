import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Guice}
import org.slf4j.{LoggerFactory, Logger}
import play.api.db.DB
import play.api.{Application, GlobalSettings}
import services._

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {
  private final val logger: Logger = LoggerFactory.getLogger(classOf[GlobalSettings])

  override def onStart(app: Application): Unit = {
    import anorm._
    import play.api.Play.current
    DB.withConnection { implicit c =>
      try {
        SQL(DBService.createSQL).execute()
        for (v <- 1 to 5) {
          SQL(DBService.insertSql(v, null, 0, 0)).execute()
        }
      } catch {
        case e: Exception => logger.error("exception caught: " + e);
      }
    }


  }

  /**
   * Bind types such that whenever TextGenerator is required, an instance of WelcomeTextGenerator will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[TextGen]).annotatedWith(Names.named("first")).to(classOf[FirstTextGenerator])
      bind(classOf[TextGen]).annotatedWith(Names.named("second")).to(classOf[SecondTextGenerator])
      bind(classOf[TextGen]).annotatedWith(Names.named("mix")).toProvider(classOf[GeneratorProvider])
      bind(classOf[DBService]).annotatedWith(Names.named("db")).toInstance(new DBService)

      val conf = new ConfMap(Map( "text.switch" -> 1))

      bind(classOf[Conf]).toInstance(conf)
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)
}
