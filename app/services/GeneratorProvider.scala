package services
import javax.inject.{Named, Inject, Provider}
import scala.util.Random

class GeneratorProvider @Inject() (@Named("welcome") tg1: TextGen,@Named("more") tg2: TextGen, conf:Conf ) extends Provider[TextGen]{
  def get: TextGen = Util.createProxy(conf.getIdx, classOf[TextGen], tg1, tg2)
}
