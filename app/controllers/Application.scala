package controllers

import service.{ProjectService, CategoryService}
import com.google.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import model.{Project, Comment}
import org.bson.types.ObjectId
import play.api.mvc.Result
import eu.henkelmann.actuarius.ActuariusTransformer


@Singleton
class Application @Inject()(val categoryService: CategoryService,
                            val projectService: ProjectService) extends BaseController {

  implicit val ctxBuilder = ViewContextBuilder(categoryService, projectService)
  private val markdownTransformer = new ActuariusTransformer

  private def withCategoryNames(p: Project) = p.copy(categoryNames = p.categories.map(categoryService.findById).flatten.map(_.name).toSet)

  private def unfriendlyString(s: String) = s.trim.replaceAllLiterally("_", " ").replaceAllLiterally("-", " ")

  def index = AsyncAction {
    implicit ctx => Ok(views.html.index.apply(projectService.all))
  }

  def project(name: String) = AsyncAction {
    implicit ctx =>
      projectService.findByName(unfriendlyString(name)).map({
        project => Ok(views.html.project(withCategoryNames(project)))
      }).getOrElse(NotFound(s"Project with name ${'"'}$name${'"'} doesn't exist"))
  }

  def projects(category: Option[String] = None) = AsyncAction {
    implicit ctx =>
      val view = views.html.projects
      category match {
        case None => Ok(view(projectService.all.map(withCategoryNames)))
        case Some(name) => categoryService.findByName(unfriendlyString(name)).map({
          category => Ok(view(projectService.findByCategory(category).map(withCategoryNames)))
        }).getOrElse(NotFound(s"Category ${'"'}$name${'"'} doesn't exist"))
      }
  }

  def deleteProject(id: ObjectId) = AsyncAction {
    implicit ctx =>
      projectService.delete(id) match {
        case Some(e) => InternalServerError(s"Can't delete project with id $id")
        case _ => Redirect(routes.Application.index())
      }
  }

  val commentForm = Form(mapping(
    "projectId" -> nonEmptyText,
    "comment" -> nonEmptyText
  )((projectId, comment) => (new ObjectId(projectId), Comment(_: String, markdownTransformer(comment))))
  ({case (a: ObjectId, b: Comment) => Some((a.toString, b.text))}))

  def addComment = Auth {
    implicit ctx =>
      val u = ctx.user.get
      AsyncAction(parse.anyContent) {
        implicit request => c =>
          commentForm.bindFromRequest.fold(
            formWithErrors => BadRequest("Validation failed"),
            value => {
              projectService.findById(value._1).fold[Result](BadRequest(s"Project with id $value._1 not found")) {
                p => projectService.update(p.copy(comments = p.comments ++ Seq(value._2(u.name.getOrElse(u.login))))) match {
                  case Some(e) => InternalServerError(s"Error updating project: ${e.getMessage}")
                  case _ => Redirect(routes.Application.project(p.urlFriendlyName).url + s"#comment${p.comments.length - 1}")
                }
              }
            }
          )
      }
  }
}