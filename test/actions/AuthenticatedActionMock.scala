package actions

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import models.User
import play.api.mvc._

class AuthenticatedActionMock(user: User) (
  implicit
  val ec: ExecutionContext,
  val bp: BodyParsers.Default
) extends AuthenticatedActionT {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {
    Future(Right(AuthenticatedRequest(user, request)))
  }

}
