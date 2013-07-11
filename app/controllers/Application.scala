package controllers

import service.{ProjectService, CategoryService}
import com.google.inject.{Inject, Singleton}
import org.bson.types.ObjectId
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class Application @Inject()(val categoryService: CategoryService,
                            val projectService: ProjectService) extends BaseController {

  def index = AsyncAction {
    Ok(views.html.index.apply)
  }

  def project(id: ObjectId) = AsyncAction {
    projectService.findById(id).map({
      project => Ok(views.html.project(project))
    }).getOrElse(NotFound(s"Project with id $id doesn't exist"))
  }

  def allProjects = projects()

  def projects(categoryId: Option[ObjectId] = None) = AsyncAction {
    val view = views.html.projects
    categoryId match {
      case None => Ok(view(projectService.all))
      case Some(id) => categoryService.findById(id).map({
        category => Ok(view(projectService.findByCategory(category)))
      }).getOrElse(NotFound(s"Category with id $categoryId doesn't exist"))
    }
  }
}
