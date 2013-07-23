package service.mongo

import com.google.inject.Inject
import service.{CategoryService, ProjectService}
import model.{MavenDependency, Category, Project}
import com.novus.salat.dao.SalatDAO
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._
import org.apache.maven.model.Model

/**
 * @author Anton Tychyna
 */
class MongoProjectService @Inject()(categoryService: CategoryService, val salatDao: SalatDAO[Project, ObjectId]) extends ProjectService with DaoCompanion[Project, ObjectId] {

  def all = companion.findAll().toList

  def findByCategory(category: Category) = companion.find(MongoDBObject("categories" -> category.id)).toList

  def findById(id: ObjectId) = companion.findOneById(id)

  def countInCategory(c: Category) = {
    def countRecursively(c: Category): Int = {
      findByCategory(c).size + c.children.map(countRecursively).sum
    }
    countRecursively(c)
  }

  def findByName(name: String, ignoreCase: Boolean) = companion.findOne(MongoDBObject(if (ignoreCase) "nameLowerCase" -> name.toLowerCase else "name" -> name))

  def save(p: Project) = {
    if (findByName(p.name).isDefined) {
      Left(new IllegalArgumentException(s"Project with name '$p.name' already exist"))
    } else {
      companion.save(p)
      Right(p)
    }
  }
}
