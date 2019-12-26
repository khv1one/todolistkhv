package actions

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import models.User
import play.api.mvc._

class UserActionMock(user: Option[User] = Option.empty) (
  implicit ec: ExecutionContext
) extends UserActionT {

  override def invokeBlock[A](
    request: Request[A],
    block: UserRequest[A] => Future[Result]
  ): Future[Result] = {
    if (user.nonEmpty) {
      block(UserRequest(user.get, request))
    } else {
      Future.successful(Results.Forbidden)
    }
  }

  private implicit val mat: ActorSystem = ActorSystem()
  private val bp = new BodyParsers.Default

  override def parser: BodyParser[AnyContent] = bp
  override protected def executionContext: ExecutionContext = ec
}
