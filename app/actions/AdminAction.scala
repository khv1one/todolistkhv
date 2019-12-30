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
class AdminAction @Inject() (
  parser: BodyParsers.Default,
  userRepo: UserRepo
)(
  implicit ec: ExecutionContext,
) extends AdminActionT {

  override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
    val admin = request.session.get(GlobalKeys.SESSION_USER_NAME_KEY) match {
      case Some(name) if name.equals("admin") => OptionT[Future, User](userRepo.userByName(name)) //заглушка вместо таблицы ролей
      case _ => OptionT[Future, User](Future(None))
    }

    val userRequest = admin.flatMap { adm =>
      OptionT.liftF(Future(UserRequest(adm, request)))
    }

    EitherT.fromOptionF(userRequest.value, Results.Forbidden).value
  }

  override protected def executionContext: ExecutionContext = ec
  override def parser: BodyParser[AnyContent] = parser
}