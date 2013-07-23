package controllers

import service.{ProjectService, CategoryService}
import com.google.inject.{Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class Application @Inject()(val categoryService: CategoryService,
                            val projectService: ProjectService) extends BaseController {

  def index = AsyncAction {
    Ok(views.html.index.apply(projectService.all))
  }

  def project(name: String) = AsyncAction {
    projectService.findByName(unfriendlyString(name)).map({
      project => Ok(views.html.project(project))
    }).getOrElse(NotFound(s"Project with name ${'"'}$name${'"'} doesn't exist"))
  }

  def allProjects = projects()

  def projects(category: Option[String] = None) = AsyncAction {
    val view = views.html.projects
    category match {
      case None => Ok(view(projectService.all))
      case Some(name) => categoryService.findByName(unfriendlyString(name)).map({
        category => Ok(view(projectService.findByCategory(category)))
      }).getOrElse(NotFound(s"Category ${'"'}$name${'"'} doesn't exist"))
    }
  }

  private def unfriendlyString(s: String) = s.trim.replaceAllLiterally("_", " ").replaceAllLiterally("-", " ")
}
