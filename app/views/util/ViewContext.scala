package views.util

/**
 * @author Anton Tychyna
 */
case class ViewContext(categories: Seq[model.Category], projectCount: model.Category => Int)
