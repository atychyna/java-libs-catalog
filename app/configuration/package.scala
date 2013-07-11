import com.google.inject.{Module, Injector, ConfigurationException, Guice}
import com.novus.salat.Context
import com.tzavellas.sse.guice.ScalaModule
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
    val module = config.getString("guice.module").map {
      i => {
        val m = runtimeMirror(getClass.getClassLoader)
        val symbol = m.classSymbol(Class.forName(i))
        val clazz = m.reflectClass(symbol)
        val constructor = clazz.reflectConstructor(symbol.toType.declaration(nme.CONSTRUCTOR).asMethod)
        constructor(Play.current).asInstanceOf[Module]
      }
    }.getOrElse {
      if (Play.isDev) new DevModule else new ProdModule
    }
    Guice.createInjector(module)
  }
}
