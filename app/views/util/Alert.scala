package views.util

import play.api.templates.Html

/**
 * @author Anton Tychyna
 */
case class Alert(text: Html, t: String = "error")