package models

import play.api.libs.json.{Format, JsObject, JsResult, JsString, JsValue, Json, OFormat, OWrites, Reads}

case class User (
  id: Long,
  username: String,
  password: String
)

object User extends Format[User] {

  implicit val jsWrite: OWrites[User] = Json.writes[User]
  implicit val jsRead: Reads[User] = reads

  override def reads(json: JsValue): JsResult[User] =
    for {
      id <- (json \ "id").validate[String]
      name <- (json \ "username").validate[String]
      pass <- (json \ "password").validate[String]
    } yield if (id.isEmpty) User(0, name, pass) else User(id.toLong, name, pass)

  override def writes(o: User): JsValue = JsObject(List(
    "id" -> JsString(o.id.toString),
    "username" -> JsString(o.username),
    "password" -> JsString(o.password)
  ))

}