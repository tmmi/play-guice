package controllers

import javax.inject.{Inject, Named, Singleton}

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import services.{Conf, ConfMap, TextGen}
/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the text generator service.
 * @param textGenerator the text generator service we wish to receive.
 */
@Singleton
class Application @Inject() (@Named("mix") textGenerator: TextGen, c: Conf) extends Controller {

  lazy val conf = c.asInstanceOf[ConfMap]

  implicit val wds = (
    (__ \ 'key).write[String] and
      (__ \ 'value).write[Int]
    ) tupled

  def index = Action {
    Ok(views.html.index(textGenerator.value))
  }

  def admin = Action {
    Ok(views.html.admin(textGenerator.value))
  }

  def listAll = Action {
    Ok(Json.toJson(conf.listAll))
  }

  def set(key: String, value: Int) = Action {
    conf.set(key,value)
    Ok("Ok")
  }

  def remove(key: String) = Action {
    conf.map.remove(key)
    Ok("Ok")
  }


}
