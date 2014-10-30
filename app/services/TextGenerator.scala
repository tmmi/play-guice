package services

import javax.inject.Singleton
import scala.util.Random

/**
 * A type declaring the interface that will be injectable.
 */

trait TextGen{
  def value : String
}

abstract class TextGenerator(val text: String) extends TextGen{
  def value = text
}

/**
 * A simple implementation of TextGenerator that we will inject.
 */
@Singleton
class WelcomeTextGenerator extends TextGenerator("Your new application is ready.")

@Singleton
class MoreTextGenerator extends TextGenerator("More.")

object Util {
  import java.lang.reflect.{Method, InvocationHandler, Proxy}

  def createProxy[I](selectFunction: => Int , interfaceClass: Class[I], proxee: I *): I = {
    //assert(interfaceClass.isInterface, "interfaceClass should be an inteface class")
    Proxy.newProxyInstance(interfaceClass.getClassLoader, Array(interfaceClass), new InvocationHandler() {
      override def invoke(proxy: Object, method: Method, args: Array[Object]) = {
        val choose:Int = selectFunction
        val idx = choose % proxee.size
        println("before selected " + idx + " size:" + proxee.size)
        val result = method.invoke(proxee(idx), args: _*)
        println("after")
        result
      }
    }).asInstanceOf[I]
  }
  //new DynamicProxy{ val dynamicProxyTarget = x }
}