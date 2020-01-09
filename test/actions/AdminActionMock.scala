package actions

import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.mvc._

class AdminActionMock(user: User) (
  implicit
  val ec: ExecutionContext,
  val bp: BodyParsers.Default
) extends AdminActionT {

  override def filter[A](request: AuthenticatedRequest[A]): Future[Option[Result]] = {
    Future(None)
  }

}