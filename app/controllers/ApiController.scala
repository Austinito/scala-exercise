package controllers

import scala.concurrent.ExecutionContext

import javax.inject.{Inject, Singleton}
import models.LoginPromptRepository
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

/**
 * This controller handles the JSON API used by client applications to display login prompts.
 */
@Singleton
class ApiController @Inject()(
                               repo: LoginPromptRepository,
                               val cc: ControllerComponents
                             )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Returns the list of existing login prompts.
   * @return A list of login prompts, in JSON format.
   */
  def getLoginPrompts = Action.async { implicit request =>
    repo.list().map { loginPrompts =>
      Ok(Json.toJson(loginPrompts))
    }
  }

  /**
   * Returns an existing login prompt.
   * @param id The unique id.
   * @return A login prompt, in JSON format.
   */
  def getLoginPrompt(id: Long) = Action.async { implicit request =>
    repo.findById(id).map {
      case Some(loginPrompt) => Ok(Json.toJson(loginPrompt))
      case None => NotFound
    }
  }

  /**
   * Returns a random login prompt.
   * @return A login prompt, in JSON format.
   */
  def getRandomLoginPrompt() = Action.async {
    repo.getRandom().map {
      case Some(loginPrompt) => Ok(Json.toJson(loginPrompt))
      case None => NotFound
    }
  }

  /**
   * Returns a random login prompt for an existing user.
   * @param userId The unique user id.
   * @return A login prompt, in JSON format.
   */
  def getRandomLoginPromptForUser(userId: Long) = Action {
    NotImplemented
  }
}
