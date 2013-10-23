package views.util

import model.User

/**
 * @author Anton Tychyna
 */
case class ViewContext(categories: Seq[model.Category],
                       user: Option[User] = None) {
  def isAnonymous = !user.isDefined
}
