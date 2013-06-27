import scala.xml.PrettyPrinter

/**
 * @author Anton Tychyna
 */
package object model {
  implicit val prettyPrinter = new PrettyPrinter(120, 4)
}
