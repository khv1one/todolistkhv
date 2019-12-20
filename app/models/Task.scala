package models

import play.api.libs.json.{Json, OFormat}

case class Task (
  id: Long,
  userId: Long,
  text: String,
  done: Boolean,
  deleted: Boolean
)

object Task {
  implicit val TaskFormat: OFormat[Task] = Json.format[Task]
}
