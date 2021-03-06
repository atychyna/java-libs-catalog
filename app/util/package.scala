import com.mongodb.WriteResult
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

  def checkError[T](r: WriteResult): Option[Exception] = {
    if (r.getLastError.ok()) {
      None
    } else {
      Some(r.getLastError.getException)
    }
  }
}
