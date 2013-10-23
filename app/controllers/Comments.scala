package controllers

import eu.henkelmann.actuarius.ActuariusTransformer
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import org.bson.types.ObjectId
import model.Comment
import play.api.mvc.Result
import com.google.inject.{Singleton, Inject}
import service.{ProjectService, CategoryService}
import play.api.Logger

/**
 * @author Anton Tychyna
 */
@Singleton
class Comments @Inject()(val categoryService: CategoryService,
                         val projectService: ProjectService) extends BaseController {
  implicit val ctxBuilder = ViewContextBuilder(categoryService, projectService)
  private val markdownTransformer = new ActuariusTransformer

  val commentForm = Form(mapping(
    "projectId" -> nonEmptyText,
    "comment" -> nonEmptyText
  )((projectId, comment) => (new ObjectId(projectId), Comment(new ObjectId, _: String, markdownTransformer(comment))))
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
                p => projectService.update(p.copy(comments = p.comments ++ Seq(value._2(u.displayName)))) match {
                  case Some(e) => InternalServerError(s"Error updating project: ${e.getMessage}")
                  case _ => Redirect(routes.Application.project(p.urlFriendlyName).url + s"#comment${p.comments.length - 1}")
                }
              }
            }
          )
      }
  }

  def deleteComment(projectId: ObjectId, commentId: ObjectId) = Auth {
    implicit ctx =>
      AsyncAction {
        Logger("play").info("Deleting " + commentId)
        projectService.findById(projectId) match {
          case Some(p) =>
            val idx = p.comments.indexWhere(_.id == commentId)
            if (idx != -1) {
              val updatedComments = p.comments.filterNot(_.id == commentId)
              projectService.update(p.copy(comments = updatedComments)) match {
                case Some(e) => InternalServerError(s"Comment with id $commentId and project ${'"'}${p.name}${'"'}: ${e.getMessage}")
                case _ =>
                  val nextCommentAnchor = if (updatedComments.isEmpty) "" else s"#comment${Math.min(idx, updatedComments.length - 1)}"
                  Redirect(routes.Application.project(p.urlFriendlyName).url + nextCommentAnchor)
              }
            } else {
              NotFound(s"Comment with id $commentId not found in project ${'"'}${p.name}${'"'}")
            }
          case _ => NotFound(s"Project with id $projectId not found")
        }
      }
  }
}
