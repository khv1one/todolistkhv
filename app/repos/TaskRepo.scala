package repos

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

import models.{Task, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton
class TaskRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)
                          (implicit ec: ExecutionContext) {

  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("user_id")
    def text = column[String]("text")
    def done = column[Boolean]("done")
    def deleted = column[Boolean]("deleted")
    def * = (id, userId, text, done, deleted) <> ((Task.apply _).tupled, Task.unapply)

    //def user = foreignKey("user_fk", userId, userRepo)(_.id)
  }

  private val tasks = TableQuery[TaskTable]


}