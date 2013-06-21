package service.mongo

import com.google.inject.Inject
import service.{CategoryService, ProjectService}
import model.{Category, Project}
import com.novus.salat.dao.SalatDAO
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._

/**
 * @author Anton Tychyna
 */
class MongoProjectService @Inject()(categoryService: CategoryService, val salatDao: SalatDAO[Project, ObjectId]) extends ProjectService with DaoCompanion[Project, ObjectId] {

  def all = companion.findAll().toList

  def findByCategory(category: Category) = companion.find(MongoDBObject("categoryId" -> category.id)).toList

  def findById(id: ObjectId) = companion.findOneById(id)

  def countInCategory(c: Category) = {
    def countRecursively(c: Category): Int = {
      findByCategory(c).size + c.children.map(countRecursively).sum
    }
    countRecursively(c)
  }

  def findByName(name: String, ignoreCase: Boolean) = companion.findOne(MongoDBObject(if (ignoreCase) "nameLowerCase" -> name.toLowerCase else "name" -> name))
}
