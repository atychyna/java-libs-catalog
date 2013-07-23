package controllers

import play.api.mvc.{Result, Action}
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import model.{SbtDependency, MavenScope, MavenDependency, Project}
import com.google.inject.{Singleton, Inject}
import service.{ProjectImporter, ProjectService, CategoryService}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.cache.Cache
import org.bson.types.ObjectId
import java.io.FileInputStream

/**
 * Add new project workflow is '''uploadPom -> reviewProject -> createProject''' (for import from Maven POM) or
 * '''{user enters data manually} -> reviewProject -> createProject'''

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
      "id" -> text,
      "name" -> nonEmptyText.verifying("Project with this name already exists", projectService.findByName(_).isEmpty),
      "url" -> nonEmptyText,
      "description" -> nonEmptyText,
      "categories" -> list(nonEmptyText).verifying("This field is required", !_.isEmpty),
      "maven" -> optional(mapping(
        "groupId" -> nonEmptyText,
        "artifactId" -> nonEmptyText,
        "version" -> nonEmptyText,
        "scope" -> nonEmptyText)
        ((groupId, artifactId, version, scope) =>
          MavenDependency(groupId, artifactId, version, MavenScope.withName(scope)))
        (d => Some(d.groupId, d.artifactId, d.version, d.scope.toString))),
      "sbt" -> optional(text.verifying("Incorrect value", SbtDependency.parse(_).successful)))
      ((id, name, url, desc, cats, maven, sbt) =>
        Project(id = new ObjectId(id), name = name, url = url, description = desc, categories = cats.map(new ObjectId(_)), mavenDependency = maven, sbtDependency = sbt.map(SbtDependency.parse(_).get)))
      (p =>
        Some(p.id.toString, p.name, p.url, p.description, p.categories.map(_.toString).toList, p.mavenDependency, p.sbtDependency.map(_.dependencyDefinition)))
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
        p => p.fold({
          e => InternalServerError(s"Error importing pom.xml: ${e.getMessage}")
        }, {
          pr => Ok(views.html.addproject(addProjectForm.fill(pr), step = 1))
        })
      }.getOrElse(BadRequest("No pom.xml uploaded"))
  }

  def reviewProject = Action {
    implicit r =>
      import play.api.Play.current
      // user submits form with project data
      addProjectForm.bindFromRequest.fold(
        // validation error(s), show em to user
        formWithErrors => {
          BadRequest(views.html.addproject(formWithErrors, step = 1))
        },
        value => {
          // step1: we need to store new project temporarily while user's reviewing it
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
              // step2: remove project stored during step1 from temp storage
              Cache.remove(i)
              projectService.save(p) match {
                case Left(e) => InternalServerError(s"Error saving $p: ${e.getMessage}")
                case Right(pr) => Ok(views.html.addprojectdone(pr)).withNewSession
              }
            }
          }.getOrElse(NotFound(s"No product with id $i found in review cache"))
      }
  }

}
