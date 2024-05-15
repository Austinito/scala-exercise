package models

import play.api.libs.json.{Json, OFormat}

/**
 * A login prompt is like a simple ad which is displayed after a user logs in.
 * @param id A unique id.
 * @param caption The caption text.
 * @param imageUrl The image url.
 * @param quietPeriodSec The quiet period in seconds.
 */
case class LoginPrompt(id: Long, caption: String, imageUrl: String, quietPeriodSec: Option[Int])

object LoginPrompt {
  implicit val loginPromptFormat: OFormat[LoginPrompt] = Json.format[LoginPrompt]
}
