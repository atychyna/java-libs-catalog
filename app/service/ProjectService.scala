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

  def countInCategory(c: Category): Int = {
    def countRecursively(c: Category): Int = {
      findByCategory(c).size + c.children.map(countRecursively).sum
    }
    countRecursively(c)
  }

  def save(p: Project): Either[Exception, Project]

  def update(p: Project): Either[Exception, Project]
}