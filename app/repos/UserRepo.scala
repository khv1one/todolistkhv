package repos

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton
class UserRepo @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username", O.Unique)
    def password = column[String]("password")
    def * = (id, username, password) <> ((User.apply _).tupled, User.unapply)
  }

  private lazy val usersTable = TableQuery[UserTable]

  def add(user: User): Future[Int] = db.run( usersTable += user )

  def users: Future[Seq[User]] = db.run(
    usersTable.result
  )


  def userById(id: Long): Future[Option[User]] = db.run {
    usersTable.filter(_.id === id).result.headOption
  }

  def userByName(username: String): Future[Option[User]] = db.run {
    usersTable.filter(_.username === username).result.headOption
  }

  def userByNameAndPassword(
    username: String,
    password: String
  ): Future[Option[User]] = db.run {
    usersTable
      .filter(user =>
        user.username === username &&
        user.password === password)
      .result.headOption
  }

  def update(user: User): Future[Int] = db.run {
    usersTable.filter(_.id === user.id).update(user)
  }

  def delete(id: Long): Future[Int] = db.run {
    usersTable.filter(_.id === id).delete
  }
}
