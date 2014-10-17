package services

import scala.util.Random

trait Conf {
  def getIdx: Int
}

class ConfRandom extends Conf {
  def getIdx = Random.nextInt(4)
}
