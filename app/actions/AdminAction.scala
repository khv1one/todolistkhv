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
class AdminAction @Inject() (
  parser: BodyParsers.Default,
  userRepo: UserRepo
)(implicit ec: ExecutionContext
) extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](
    request: Request[A],
    block: Request[A] => Future[Result]
  ): Future[Result] = {

    val admin = request.session.get(GlobalKeys.SESSION_USER_ID_KEY) match {
      case Some(_) => userRepo.userByName("admin") //заглушка вместо таблицы ролей
      case _ => OptionT[Future, User](Future(None))
    }

    admin
      .flatMap( _ => OptionT.liftF(block (request)) )
      .getOrElse(Results.Forbidden)
  }
}