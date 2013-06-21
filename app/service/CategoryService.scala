package service

import model.Category
import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
trait CategoryService {
  def all: Seq[Category]

  def findById(id: ObjectId): Option[Category]

  def findByName(name: String, ignoreCase: Boolean = true): Option[Category]
}