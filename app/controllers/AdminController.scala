package controllers

import scala.concurrent.{ExecutionContext, Future}

import javax.inject.{Inject, Singleton}
import models.{LoginPrompt, LoginPromptRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}

/**
 * This controller handles the web-based admin UI for creating, updating and deleting login prompts.
 */
@Singleton
class AdminController @Inject() (
                                  repo: LoginPromptRepository,
                                  val cc: MessagesControllerComponents)(
                                  implicit ec: ExecutionContext
                                ) extends MessagesAbstractController(cc)
{
  /**
   * The web form for creating and updating login prompts.
   */
  val loginPromptForm = Form(
    mapping(
      "caption" -> nonEmptyText,
      "imageUrl" -> nonEmptyText
    )(LoginPromptData.apply)(LoginPromptData.unapply)
  )

  /**
   * Displays a list of login prompts.
   */
  def index() = Action.async {
    repo.list().map { loginPrompts =>
      Ok(views.html.list(loginPrompts))
    }
  }

  /**
   * Displays form for creating a new login prompt.
   */
  def add() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.form(None, loginPromptForm))
  }

  /**
   * Handles submitted form for a new login prompt.
   */
  def addPost() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    loginPromptForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.form(None, formWithErrors)))
      },
      data =>
        repo
          .create(data.caption, data.imageUrl)
          .map(_ =>Redirect(routes.AdminController.index()))
    )
  }

  /**
   * Displays form for editing an existing login prompt.
   */
  def edit(id: Long) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repo.findById(id).map {
      case Some(loginPrompt) =>
        val data = LoginPromptData(
          caption = loginPrompt.caption,
          imageUrl = loginPrompt.imageUrl
        )
        val form = loginPromptForm.fill(data)
        Ok(views.html.form(Some(id), form))
      case None =>
        NotFound
    }
  }

  /**
   * Handles submitted form for an existing login prompt.
   */
  def editPost(id: Long) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    loginPromptForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.form(Some(id), formWithErrors)))
      },
      data =>
        repo
          .update(LoginPrompt(id, caption = data.caption, imageUrl = data.imageUrl))
          .map( _ => Redirect(routes.AdminController.index()))
    )
  }

  /**
   * Deletes an existing login prompt.
   */
  def delete(id: Long) = Action.async {
    repo.delete(id).map { success =>
      if (success) Redirect(routes.AdminController.index())
      else NotFound
    }
  }
}

case class LoginPromptData(
                            caption: String,
                            imageUrl: String
                          )
