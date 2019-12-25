package actions

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import cats.data.OptionT
import cats.instances.future._
import models.User
import play.api.mvc._
import repos.UserRepo
import utils.GlobalKeys

@Singleton
class UserAction @Inject() (
  parser: BodyParsers.Default,
  userRepo: UserRepo,
  )(implicit ec: ExecutionContext
  ) extends UserActionT {

  override def invokeBlock[A](
    request: Request[A],
    block: UserRequest[A] => Future[Result]
  ): Future[Result] = {

    val user = request.session.get(GlobalKeys.SESSION_USER_ID_KEY) match {
      case Some(value) => userRepo.userByName(value)
      case _ => OptionT[Future, User](Future(None))
    }

    user
      .flatMap( usr => OptionT.liftF( block (UserRequest(usr, request) )))
      .getOrElse(Results.Unauthorized)

  }

  override def parser: BodyParser[AnyContent] = parser
  override protected def executionContext: ExecutionContext = ec
}
