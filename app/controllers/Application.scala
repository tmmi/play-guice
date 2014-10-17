package controllers

import play.api.mvc._
import javax.inject.{Named, Qualifier, Singleton, Inject}
import services.{TextGen, TextGenerator}

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the text generator service.
 * @param textGenerator the text generator service we wish to receive.
 */
@Singleton
class Application @Inject() (@Named("all") textGenerator: TextGen) extends Controller {
  
  def index = Action {
    Ok(views.html.index(textGenerator.value))
  }
  
}
