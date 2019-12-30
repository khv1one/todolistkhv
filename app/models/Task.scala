package models
import play.api.libs.json._

case class Task (
  id: Long,
  userId: Long,
  text: String,
  done: Boolean,
  deleted: Boolean
)

object Task {
  implicit val reads: Reads[Task] = for {
      id <- (__ \ "id").readWithDefault[Long](0L)
      userId <- (__ \ "userId").read[Long]
      text <- (__ \ "text").read[String]
      done <- (__ \ "done").readWithDefault[Boolean](false)
      deleted <- (__ \ "deleted").readWithDefault[Boolean](false)
    } yield Task(id, userId, text, done, deleted)

  implicit val writes: Writes[Task] = task => Json.obj(
    "id" -> task.id,
    "userId" -> task.userId,
    "text" -> task.text,
    "done" -> task.done,
    "deleted" -> task.deleted,
  )
}