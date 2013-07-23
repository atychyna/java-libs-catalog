package service.mongo

import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import com.google.inject.Inject
import service.{CategoryService, InitialData}
import model.{MavenScope => Scope, _}
import SbtDependency.fromMavenDependency
import com.google.inject.name.Named

class MongoDataImporter @Inject()(categoryDao: SalatDAO[Category, ObjectId],
                                  projectDao: SalatDAO[Project, ObjectId],
                                  categoryService: CategoryService,
                                  @Named("dropExisting") dropExisting: Boolean) extends InitialData {

  private lazy val categories = Seq(
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

  private lazy val projects = Seq(
    Project(name = "Apache Maven",
      url = "http://maven.apache.org/",
      description = "Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information.",
      categories = category("build tools"),
      scm = Some("https://git-wip-us.apache.org/repos/asf/maven.git")),
    Project(name = "JUnit",
      url = "http://junit.org/",
      description = "JUnit is a simple framework to write repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks.",
      categories = category("unit testing"),
      scm = Some("https://github.com/junit-team/junit.git"),
      wiki = Some("https://github.com/junit-team/junit/wiki"),
      mavenDependency = Some(MavenDependency("junit", "junit", "4.11", Scope.Test))),
    Project(name = "Mongo DB",
      url = "http://maven.apache.org/",
      description = "MongoDB (from \"humongous\") is an open-source document database, and the leading NoSQL database. Written in C++.",
      languages = ProjectLang.ValueSet(ProjectLang.Java, ProjectLang.Scala),
      categories = category("nosql"),
      scm = Some("https://github.com/mongodb"))
  )

  private def category(name: String): Seq[ObjectId] = {
    categoryService.findByName(name).map(c => Seq(c.id)).getOrElse(throw new IllegalArgumentException(s"Category ${'"'}$name${'"'} doesn't exist"))
  }

  override def apply() {
    if (dropExisting) {
      categoryDao.collection.drop()
      projectDao.collection.drop()
    }
    if (categoryDao.count() == 0) categories foreach categoryDao.insert
    if (projectDao.count() == 0) projects foreach projectDao.insert
  }
}
