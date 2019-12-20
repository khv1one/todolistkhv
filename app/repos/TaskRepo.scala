package repos

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import models.{Task, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

@Singleton
class TaskRepo @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("userId")
    def text = column[String]("text")
    def done = column[Boolean]("done")
    def deleted = column[Boolean]("deleted")
    def * = (id, userId, text, done, deleted) <> ((Task.apply _).tupled, Task.unapply)
  }

  private lazy val tasksTable = TableQuery[TaskTable]

  def add(task: Task) = db.run (tasksTable += task)

  def tasks: Future[Seq[Task]] = db.run (tasksTable.result)

  def tasksByUserId(userId: Long): Future[Seq[Task]] = db.run {
    tasksTable.filter(_.userId === userId).result
  }

}