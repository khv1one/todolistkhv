package models

import play.api.libs.json.{Format, JsObject, JsResult, JsString, JsValue, Json, OFormat, OWrites, Reads}

case class User (
  id: Long,
  username: String,
  password: String
)

object User extends Format[User] {

  implicit val jsWrite: OWrites[User] = writes
  implicit val jsRead: Reads[User] = reads

  override def reads(json: JsValue): JsResult[User] =
    for {
      id <- (json \ "id").validate[Long]
      name <- (json \ "username").validate[String]
      pass <- (json \ "password").validate[String]
    } yield User(id, name, pass)

  override def writes(user: User): JsObject = Json.obj(
    "id" -> user.id,
    "username" -> user.username,
    "password" -> user.password,
  )

}