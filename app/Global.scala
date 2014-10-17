import com.google.inject.name.Names
import com.google.inject.{Guice, AbstractModule}
import play.api.GlobalSettings
import services._

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  /**
   * Bind types such that whenever TextGenerator is required, an instance of WelcomeTextGenerator will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[TextGen]).annotatedWith(Names.named("welcome")).to(classOf[WelcomeTextGenerator])
      bind(classOf[TextGen]).annotatedWith(Names.named("more")).to(classOf[MoreTextGenerator])
      bind(classOf[TextGen]).annotatedWith(Names.named("all")).toProvider(classOf[GeneratorProvider])
      bind(classOf[Conf]).to(classOf[ConfRandom])
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)
}
