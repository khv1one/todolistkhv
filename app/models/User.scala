package models

import play.api.libs.json._

case class User (
  id: Long,
  username: String,
  password: String
)

object User  {
  implicit val reads: Reads[User] = for {
      id <- (__ \ "id").readWithDefault[Long](0L)
      name <- (__ \ "username").read[String]
      pass <- (__ \ "password").read[String]
    } yield User(id, name, pass)

  implicit val writes: Writes[User] = user => Json.obj(
    "id" -> user.id,
    "username" -> user.username,
    "password" -> user.password,
  )
}