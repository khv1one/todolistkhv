package actions

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import cats.data.{EitherT, OptionT}
import cats.instances.future._
import models.User
import play.api.mvc._
import repos.UserRepo
import utils.GlobalKeys

@Singleton
class AuthenticatedAction @Inject() (
  userRepo: UserRepo,
)(
  implicit
  val ec: ExecutionContext,
  val bp: BodyParsers.Default
) extends AuthenticatedActionT {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {
    val user = request.session.get(GlobalKeys.SESSION_USER_NAME_KEY) match {
      case Some(value) => OptionT[Future, User](userRepo.userByName(value))
      case _ => OptionT[Future, User](Future(None))
    }

    val userRequest = user.flatMap { user =>
      OptionT.liftF(Future(AuthenticatedRequest(user, request)))
    }

    EitherT.fromOptionF(userRequest.value, Results.Unauthorized).value
  }

}
