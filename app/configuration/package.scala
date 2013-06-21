import com.google.inject.{Injector, ConfigurationException, Guice}
import com.novus.salat.Context
import play.api._
import play.api.Play.current
import scala.reflect.runtime.universe._
import scala.util.control.Exception._

/**
 * @author Anton Tychyna
 */
package object configuration {

  implicit class GuiceInjector(i: Injector) {
    private val m = runtimeMirror(getClass.getClassLoader)

    def bean[T: TypeTag]: Option[T] = catching(classOf[ConfigurationException]).opt[T](i.getInstance(m.runtimeClass(typeOf[T].typeSymbol.asClass)).asInstanceOf[T])

    def bean[T](clazz: Class[T]): Option[T] = catching(classOf[ConfigurationException]).opt[T](i.getInstance(clazz))
  }

  implicit val context = {
    val context = new Context {
      val name = "global"
    }
    context.registerGlobalKeyOverride(remapThis = "id", toThisInstead = "_id")
    context.registerClassLoader(Play.classloader)
    context
  }

  val config = Play.configuration

  val injector = {
    Play.isDev match {
      case true => Guice.createInjector(new DevModule)
      case false => Guice.createInjector(new ProdModule)
    }
  }
}
