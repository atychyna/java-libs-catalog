package views.util

import views.html.helper.{FieldElements, FieldConstructor}
import views.html.util.bootstrap.twitterBootstrapFieldConstructor

/**
 * @author Anton Tychyna
 */
package object bootstrap {
  implicit val twitterBootstrapField = FieldConstructor(twitterBootstrapFieldConstructor.f)
}
