package repos

import javax.inject.{Inject, Singleton}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton
class UserRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username", O.Unique)
    def password = column[String]("password")
    def * = (id, username, password) <> ((User.apply _).tupled, User.unapply)
  }

  private lazy val users = TableQuery[UserTable]

  def addUser(user: User) = db.run {
    users += user
  }
}
