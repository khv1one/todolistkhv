package actions

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import cats.data.{EitherT, OptionT}
import cats.instances.future._
import models.User
import org.checkerframework.checker.units.qual.A
import play.api.mvc._
import repos.UserRepo
import utils.GlobalKeys

@Singleton
class UserAction @Inject() (
  parser: BodyParsers.Default,
  userRepo: UserRepo,
)(
  implicit ec: ExecutionContext
) extends UserActionT {

  override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
    val user = request.session.get(GlobalKeys.SESSION_USER_NAME_KEY) match {
      case Some(value) => OptionT[Future, User](userRepo.userByName(value))
      case _ => OptionT[Future, User](Future(None))
    }

    val userRequest = user.flatMap { user =>
      OptionT.liftF(Future(UserRequest(user, request)))
    }

    EitherT.fromOptionF(userRequest.value, Results.Unauthorized).value
  }

  override def parser: BodyParser[AnyContent] = parser
  override protected def executionContext: ExecutionContext = ec
}
