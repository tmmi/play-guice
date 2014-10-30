package services

import scala.util.Random

trait Conf {
  def getIdx(key: String): Int
}

class ConfRandom extends Conf {
  def getIdx(key: String) = Random.nextInt(4)
}

class ConfMap(m : Map[String,Int]) extends Conf {
  val map = scala.collection.mutable.Map[String,Int]() ++ m

  def getIdx(key: String) = map(key)

  def set(key: String, value :Int) = {
    map.put(key,value)
  }

  def listAll = map
}


