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
}
