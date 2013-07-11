import com.novus.salat.dao.SalatDAO
import com.tzavellas.sse.guice.ScalaModule
import model.{Project, Category}
import org.bson.types.ObjectId
import service.maven.MavenImporter
import service.{InitialData, ProjectImporter, ProjectService, CategoryService}
import service.mongo.{MongoDataImporter, MongoProjectService, MongoCategoryService}
import play.api.Application
import se.radley.plugin.salat._
import configuration.context

/**
 * @author Anton Tychyna
 */
class TestModule(implicit app: Application) extends ScalaModule {
  def configure() {
    bind[SalatDAO[Category, ObjectId]].toInstance(new SalatDAO[Category, ObjectId](mongoCollection("testcategories")) {})
    bind[SalatDAO[Project, ObjectId]].toInstance(new SalatDAO[Project, ObjectId](mongoCollection("testprojects")) {})
    bind[CategoryService].to[MongoCategoryService]
    bind[ProjectService].to[MongoProjectService]
    bind[ProjectImporter].to[MavenImporter]
    bind[InitialData].to[MongoDataImporter]
    bindConstant().annotatedWithName("dropExisting").to(true)
  }
}
