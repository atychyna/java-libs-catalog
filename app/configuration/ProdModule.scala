package configuration

import com.tzavellas.sse.guice.ScalaModule
import service.{ProjectService, CategoryService}
import model.{Project, Category}
import service.mongo.{MongoProjectService, MongoCategoryService}
import se.radley.plugin.salat._
import play.api.Application
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
class ProdModule(implicit app: Application) extends ScalaModule {
  def configure() {
    bind[SalatDAO[Category, ObjectId]].toInstance(new SalatDAO[Category, ObjectId](mongoCollection("categories")) {})
    bind[SalatDAO[Project, ObjectId]].toInstance(new SalatDAO[Project, ObjectId](mongoCollection("projects")) {})
    bind[CategoryService].to[MongoCategoryService]
    bind[ProjectService].to[MongoProjectService]
  }
}




