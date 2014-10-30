package services
import javax.inject.{Named, Inject, Provider}
import scala.util.Random

class GeneratorProvider @Inject() (@Named("first") tg1: TextGen,@Named("second") tg2: TextGen, conf:Conf ) extends Provider[TextGen]{
  def get: TextGen = Util.createProxy(conf.getIdx("text.switch"), classOf[TextGen], tg1, tg2)
}
