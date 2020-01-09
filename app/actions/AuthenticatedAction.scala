package actions

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import cats.data.EitherT
import cats.instances.future._
import play.api.mvc._
import repos.UserRepo
import utils.GlobalKeys

@Singleton
class AuthenticatedAction @Inject() (
  userRepo: UserRepo,
)(
  implicit
  val executionContext: ExecutionContext,
  val parser: BodyParsers.Default
) extends AuthenticatedActionT {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {
    (for {
      userName <- EitherT.fromOption[Future](request.session.get(GlobalKeys.SESSION_USER_NAME_KEY), "Bad key")
      user <- EitherT.fromOptionF(userRepo.userByName(userName), "Not found")
    } yield new AuthenticatedRequest(user, request))
      .leftMap { ex =>
        println(ex)
        Results.Unauthorized
      }
      .value
  }
}
