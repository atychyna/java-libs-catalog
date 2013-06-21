package service.mongo

import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import com.google.inject.Inject
import service.DataImporter
import model.{Project, Category}

class MongoDataImporter @Inject()(categoryDao: SalatDAO[Category, ObjectId], projectDao: SalatDAO[Project, ObjectId]) extends DataImporter {
  private val categories = Seq(
    Category(name = "Development Tools",
      children = Seq(
        Category(name = "Build Tools"),
        Category(name = "IDE"),
        Category(name = "Continuous Integration"),
        Category(name = "Version control")
      )),
    Category(name = "Quality Assurance",
      children = Seq(
        Category(name = "Unit Testing"),
        Category(name = "Integration Testing"),
        Category(name = "Load Testing"),
        Category(name = "Version control")
      )),
    Category(name = "Database",
      children = Seq(
        Category(name = "Relational"),
        Category(name = "NoSQL"),
        Category(name = "Graph")
      ))
  )

  private val projects = Seq(
    Project(name = "Apache Maven", url = "http://maven.apache.org/", categoryId = category("build tools")),
    Project(name = "JUnit", url = "http://maven.apache.org/", categoryId = category("unit testing")),
    Project(name = "Mongo DB", url = "http://maven.apache.org/", categoryId = category("nosql"))
  )

  private def category(name: String): ObjectId = {
    def categoryR(cats: Seq[Category]): Option[Category] = {
      cats match {
        case h :: tail if h.name.equalsIgnoreCase(name) => Some(h)
        case h :: tail => {
          val ch = categoryR(h.children)
          ch orElse categoryR(tail)
        }
        case _ => None
      }
    }
    categoryR(categories).map(_.id).getOrElse(throw new IllegalArgumentException(s"Category $name doesn't exist"))
  }

  def importData() {
    if (categoryDao.count() == 0 && projectDao.count() == 0) {
      categories foreach categoryDao.insert
      projects foreach projectDao.insert
    }
  }
}
