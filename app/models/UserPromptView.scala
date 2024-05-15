package models

import java.time.Instant

import play.api.libs.json.{Json, OFormat}

case class UserPromptView(userId: Long, promptId: Long, quietUntil: Instant)

object UserPromptView {
  implicit val userPromptViewFormat: OFormat[UserPromptView] = Json.format[UserPromptView]
}
