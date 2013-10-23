package controllers

import service.{ProjectService, CategoryService}
import com.google.inject.{Inject, Singleton}
import model.Project
import views.util.Alert
import play.api.templates.Html


@Singleton
class Application @Inject()(val categoryService: CategoryService,
                            val projectService: ProjectService) extends BaseController {

  implicit val ctxBuilder = ViewContextBuilder(categoryService, projectService)

  private def withCategoryNames(p: Project) = p.copy(categoryNames = p.categories.map(categoryService.findById).flatten.map(_.name).toSet)

  private def unfriendlyString(s: String) = s.trim.replaceAllLiterally("_", " ").replaceAllLiterally("-", " ")

  def index = AsyncAction {
    implicit ctx => Ok(views.html.index.apply(projectService.all))
  }

  def project(name: String, isNewProject: Boolean = false) = AsyncAction {
    implicit ctx =>
      projectService.findByName(unfriendlyString(name)).map({
        p => Ok(views.html.project(withCategoryNames(p), if (isNewProject) Seq(Alert(Html(s"Project <strong>${p.name}</strong> successfully created."), "success")) else Seq()))
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

  def deleteProject(name: String) = Auth {
    implicit ctx => AsyncAction {
      projectService.findByName(name).map(p =>
        projectService.delete(p.id) match {
          case Some(e) => InternalServerError(s"Can't delete project ${'"'}$name${'"'}: $e.getMessage")
          case _ => Redirect(routes.Application.index())
        }).getOrElse(NotFound(s"Project ${'"'}$name${'"'} not found"))
    }
  }
}