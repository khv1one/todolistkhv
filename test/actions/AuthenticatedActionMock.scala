package actions

import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.mvc._
import cats.implicits._

class AuthenticatedActionMock(user: User) (
  implicit
  val executionContext: ExecutionContext,
  val parser: BodyParsers.Default
) extends AuthenticatedActionT {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {
    new AuthenticatedRequest(user, request)
      .asRight
      .pure[Future]
  }

}
