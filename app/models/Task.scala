package models

import play.api.libs.json.{Format, JsObject, JsResult, JsString, JsValue, Json, OFormat, OWrites, Reads}

case class Task (
  id: Long,
  userId: Long,
  text: String,
  done: Boolean,
  deleted: Boolean
)

object Task extends Format[Task] {

  implicit val jsWrite: OWrites[Task] = writes
  implicit val jsRead: Reads[Task] = reads

  override def reads(json: JsValue): JsResult[Task] =
    for {
      id <- (json \ "id").validate[String]
      userId <- (json \ "userId").validate[String]
      text <- (json \ "text").validate[String]
      done <- (json \ "done").validate[Boolean]
      deleted <- (json \ "deleted").validate[Boolean]
    } yield Task(id.toLong, userId.toLong, text, done, deleted)

  override def writes(o: Task): JsObject = Json.obj(
    "id" -> JsString(o.id.toString),
    "userId" -> JsString(o.userId.toString),
    "text" -> JsString(o.text),
    "done" -> JsString(o.done.toString),
    "deleted" -> JsString(o.deleted.toString),
  )

}