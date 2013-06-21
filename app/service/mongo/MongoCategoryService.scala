package service.mongo

import com.novus.salat.dao.SalatDAO
import model.Category
import com.google.inject.Inject
import service.CategoryService
import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
class MongoCategoryService @Inject()(val salatDao: SalatDAO[Category, ObjectId]) extends CategoryService with DaoCompanion[Category, ObjectId] {

  private def findRecursively(f: Category => Boolean)(cs: Stream[Category]): Option[Category] = {
    cs match {
      case h #:: tail if f(h) => Some(h)
      case h #:: tail => {
        val ch = findRecursively(f)(h.children.toStream)
        ch orElse findRecursively(f)(tail)
      }
      case _ => None
    }
  }

  def all = companion.findAll().toList

  def findById(id: ObjectId) = findRecursively(_.id == id)(companion.findAll().toStream)

  def findByName(name: String, ignoreCase: Boolean) = findRecursively(if (ignoreCase) _.name.equalsIgnoreCase(name) else _.name.equals(name))(companion.findAll().toStream)
}