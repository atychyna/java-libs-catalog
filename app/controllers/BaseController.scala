package controllers

import play.api.mvc._
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import model.User
import views.util.ViewContext
import scala.Some
import model.Category
import service.{ProjectService, CategoryService}

/**
 * @author Anton Tychyna
 */
trait BaseController extends Controller {
  def AsyncAction[A](bp: BodyParser[A])(f: Request[A] => ViewContext => Result)(implicit b: ViewContextBuilder): Action[A] = Action(bp) {
    request =>
      Async {
        val ctx = b.build
        val context = userFromRequest(request).fold(ctx) {u => ctx.copy(user = Some(u))}
        future(f(request)(context))
      }
  }

  def AsyncAction(f: ViewContext => Result)(implicit b: ViewContextBuilder): Action[AnyContent] = AsyncAction(parse.anyContent)(_ => f)

  def Auth(f: ViewContext => Action[AnyContent])(implicit b: ViewContextBuilder): Action[AnyContent] = {
    Action {
      request => userFromRequest(request).fold[Result](Forbidden("You're not logged in")) {
        u =>
          val ctx = b.build
          val newCtx = ctx.copy(user = Some(u))
          f(newCtx)(request)
      }
    }
  }

  def userFromRequest(r: Request[_]): Option[User] = Some(User("test", "test"))
}

trait ViewContextBuilder {
  def build: ViewContext
}

object ViewContextBuilder {
  def apply(categoryService: CategoryService, projectService: ProjectService) = new ViewContextBuilder {
    private def withProjectCounts(c: Category): Category =
      c.copy(projectCount = Some(projectService.countInCategory(c)), children = c.children.map(withProjectCounts))

    val build = ViewContext(categoryService.all.map(withProjectCounts))
  }
}