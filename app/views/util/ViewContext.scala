package views.util

import org.bson.types.ObjectId
import model.Category
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

/**
 * @author Anton Tychyna
 */
case class ViewContext(categories: Seq[model.Category],
                       projectCount: Category => Int,
                       categoryById: ObjectId => Option[Category],
                       dateTimeFormat: DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM yyyy"))
