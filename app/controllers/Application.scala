package controllers

import play.api.mvc._
import service.{ProjectService, CategoryService}
import com.google.inject.{Inject, Singleton}
import model.Category
import org.bson.types.ObjectId
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class Application @Inject()(categoryService: CategoryService, projectService: ProjectService) extends Controller {
  private def countProjectsInCategory(c: Category) = projectService.countInCategory(c)

  def index = AsyncAction {
    Ok(views.html.index(categoryService.all, countProjectsInCategory))
  }

  def project(id: ObjectId) = AsyncAction {
    projectService.findById(id).map({
      project => Ok(views.html.project(categoryService.all, countProjectsInCategory, project))
    }).getOrElse(NotFound(s"Project with id $id doesn't exist"))
  }

  def projects(categoryId: Option[ObjectId]) = AsyncAction {
    categoryId match {
      case None => Ok(views.html.projects(categoryService.all, countProjectsInCategory, projectService.all))
      case Some(id) => categoryService.findById(id).map({
        category => Ok(views.html.projects(categoryService.all, countProjectsInCategory, projectService.findByCategory(category)))
      }).getOrElse(NotFound(s"Category with id $categoryId doesn't exist"))
    }
  }

  def AsyncAction(f: => Result): Action[AnyContent] = Action {
    Async {
      future {
        f
      }
    }
  }

}
