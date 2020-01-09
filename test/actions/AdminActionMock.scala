package actions

import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.mvc._

class AdminActionMock(user: User) (implicit val executionContext: ExecutionContext) extends AdminActionT {
  override def filter[A](request: AuthenticatedRequest[A]): Future[Option[Result]] = {
    Future.successful(None)
  }
}
