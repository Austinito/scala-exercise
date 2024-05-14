package models

import play.api.libs.json.{Json, OFormat}

case class UserPromptView(userId: Long, promptId: Long, lastShown: Long)

object UserPromptView {
  implicit val userPromptViewFormat: OFormat[UserPromptView] = Json.format[UserPromptView]
}
