package controllers

import play.api.mvc._
import service.{ProjectService, CategoryService}
import com.google.inject.{Inject, Singleton}
import model.Category
import org.bson.types.ObjectId
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import views.ViewContext

@Singleton
class Application @Inject()(categoryService: CategoryService, projectService: ProjectService) extends Controller {
  private def countProjectsInCategory(c: Category) = projectService.countInCategory(c)

  def index = AsyncAction {
    Ok(views.html.index(ViewContext(categoryService.all, countProjectsInCategory)))
  }

  def project(id: ObjectId) = AsyncAction {
    projectService.findById(id).map({
      project => Ok(views.html.project(ViewContext(categoryService.all, countProjectsInCategory))(project))
    }).getOrElse(NotFound(s"Project with id $id doesn't exist"))
  }

  def addProject = Action {
    Ok(views.html.addproject(ViewContext(categoryService.all, countProjectsInCategory)))
  }

  def projects(categoryId: Option[ObjectId]) = AsyncAction {
    val view = views.html.projects(ViewContext(categoryService.all, countProjectsInCategory))_
    categoryId match {
      case None => Ok(view(projectService.all))
      case Some(id) => categoryService.findById(id).map({
        category => Ok(view(projectService.findByCategory(category)))
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
