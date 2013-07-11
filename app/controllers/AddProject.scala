package controllers

import play.api.mvc.{Result, Action}
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import model.Project
import com.google.inject.{Singleton, Inject}
import service.{ProjectImporter, ProjectService, CategoryService}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.cache.Cache
import org.bson.types.ObjectId
import play.api.Logger
import java.io.FileInputStream

/**
 * @author Anton Tychyna
 */
@Singleton
class AddProject @Inject()(val categoryService: CategoryService,
                           val projectService: ProjectService,
                           val projectImporter: ProjectImporter) extends BaseController {
  val SessionKeyReviewProject = "_projectid"
  val CacheKeyPrefixReviewProject = "review.item."
  val addProjectForm = Form(
    mapping(
      "Id" -> text,
      "Name" -> nonEmptyText,
      "Url" -> nonEmptyText,
      "Description" -> nonEmptyText
    )((id, name, url, desc) =>
      Project(id = new ObjectId(id), name = name, url = url, description = desc))
      (p =>
        Some(p.id.toString, p.name, p.url, p.description))
  )

  def index = Action {
    implicit r =>
      Ok(views.html.addproject(addProjectForm.fill(Project(name = "", description = "", url = "")), step = 0))
  }

  def uploadPom = AsyncAction(parse.multipartFormData) {
    implicit request =>
      request.body.file("pom").map {
        p => projectImporter.importFromMavenPOM(new FileInputStream(p.ref.file))
      }.map {
        p => Ok(views.html.addproject(addProjectForm.fill(p), step = 1))
      }.getOrElse(BadRequest("Error importing pom.xml"))
  }

  def reviewProject = Action {
    implicit r =>
      import play.api.Play.current
      addProjectForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.addproject(formWithErrors, step = 1))
        },
        value => {
          Cache.set(CacheKeyPrefixReviewProject + value.id, value)
          Ok(views.html.addproject(addProjectForm.fill(value), step = 2))
            .withSession(SessionKeyReviewProject -> value.id.toString)
        }
      )
  }

  def createProject = AsyncAction {
    implicit r =>
      import play.api.Play.current
      session.get(SessionKeyReviewProject).fold[Result](BadRequest("No project under review found in session")) {
        i =>
          Cache.getAs[Project](CacheKeyPrefixReviewProject + i).map {
            p => {
              Cache.remove(i)
              projectService.save(p)
              Ok(views.html.addprojectdone(p)).withNewSession
            }
          }.getOrElse(NotFound(s"No product with id $i found in review cache"))
      }
  }

}
