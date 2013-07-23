package controllers

import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import scala.concurrent._
import model.Category
import views.util.ViewContext
import service.{CategoryService, ProjectService}

/**
 * @author Anton Tychyna
 */
trait BaseController extends Controller {
  def AsyncAction(f: => Result)(implicit execctx: scala.concurrent.ExecutionContext): Action[AnyContent] = AsyncAction(parse.anyContent)(_ => f)

  def AsyncAction(f: Request[AnyContent] => Result)(implicit execctx: scala.concurrent.ExecutionContext): Action[AnyContent] = AsyncAction(parse.anyContent)(f)

  def AsyncAction[A](bp: BodyParser[A])(f: Request[A] => Result)(implicit execctx: scala.concurrent.ExecutionContext): Action[A] = Action(bp) {
    request =>
      Async {
        future {
          f(request)
        }
      }
  }

  private def countProjectsInCategory(c: Category) = projectService.countInCategory(c)

  implicit def viewContext: ViewContext = {
    ViewContext(categoryService.all, countProjectsInCategory, categoryService.findById)
  }

  def projectService: ProjectService

  def categoryService: CategoryService
}
