package views.util

import model.User
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

/**
 * @author Anton Tychyna
 */
case class ViewContext(categories: Seq[model.Category],
                       dateTimeFormat: DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM yyyy"),
                       user: Option[User] = None) {
  def isAnonymous = !user.isDefined
}
