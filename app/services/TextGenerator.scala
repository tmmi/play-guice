package services

import javax.inject.Singleton

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
class FirstTextGenerator extends TextGenerator("First welcome text generator.")

@Singleton
class SecondTextGenerator extends TextGenerator("Second text generator.")

object Util {
  import java.lang.reflect.{InvocationHandler, Method, Proxy}

  def createProxy[I](selectFunction: => Int , interfaceClass: Class[I], proxee: I *): I = {
    assert(interfaceClass.isInterface, "interfaceClass should be an inteface class")
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