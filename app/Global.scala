import com.google.inject.{Injector, Key, Guice}
import com.novus.salat.dao.SalatDAO
import model.{Project, Category}
import org.bson.types.ObjectId
import play.api.{Logger, PlayException, Application, GlobalSettings}
import play.{Play}
import service.{ProjectService, InitialData}
import service.mongo.{MongoDataImporter, MongoCategoryService, MongoProjectService}
import _root_.configuration._

object Global extends GlobalSettings {
  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.bean(clazz).getOrElse(throw new PlayException("Can't find Guice binding for controller", s"Can't find binding for class $clazz.getName"))
  }

  override def onStart(app: Application) {
    injector.bean[InitialData].map(_.apply())
  }
}