package model

import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
case class Category(id: ObjectId = new ObjectId, name: String, isTopLevel: Boolean = false, children: Seq[Category] = Seq.empty) {
  val urlFriendlyName = name.toLowerCase.replaceAllLiterally(" ", "-")
}