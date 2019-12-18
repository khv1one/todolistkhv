package models

import play.api.libs.json.{Json, OFormat}

case class User (
  id: Long,
  username: String,
  password: String
)

object User {
  implicit val TaskFormat: OFormat[User] = Json.format[User]
}