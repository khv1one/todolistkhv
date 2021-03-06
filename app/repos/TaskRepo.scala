package repos

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import models.Task
import play.api.db.slick.DatabaseConfigProvider
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

  def add(task: Task): Future[Int] = db.run (tasksTable += task)

  def tasks: Future[Seq[Task]] = db.run (tasksTable.result)

  def taskById(id: Long): Future[Option[Task]] = db.run {
    tasksTable.filter(_.id === id).result.headOption
  }

  def tasksByUserId(userId: Long): Future[Seq[Task]] = db.run {
    tasksTable.filter(_.userId === userId).result
  }

  def update(task: Task): Future[Int] = db.run {
    tasksTable.filter(_.id === task.id).update(task)
  }

  def delete(id: Long): Future[Int] = db.run {
    tasksTable.filter(_.id === id).delete
  }

}