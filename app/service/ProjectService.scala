package service

import model.{Category, Project}
import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
trait ProjectService {
  def all: Seq[Project]

  def findByCategory(c: Category): Seq[Project]

  def findById(id: ObjectId): Option[Project]

  def findByName(name: String, ignoreCase: Boolean = true): Option[Project]

  def countInCategory(c: Category): Int

  def save(p: Project): Project
}