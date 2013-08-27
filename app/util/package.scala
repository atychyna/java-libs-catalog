import com.mongodb.WriteResult
import java.lang.RuntimeException
import scala.language.reflectiveCalls
/**
 * @author Anton Tychyna
 */
package object util {
  def using[T <: {def close() : Unit}](closeable: T)(f: T => Unit) {
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
  }

  def stringForUrl(s: String) = s.toLowerCase.replaceAllLiterally(" ", "-")

  def checkError[T](r: WriteResult)(t: => T): Either[Exception, T] = {
    if (r.getError != null) {
      Left(new RuntimeException(r.getError))
    } else {
      Right(t)
    }
  }
}
