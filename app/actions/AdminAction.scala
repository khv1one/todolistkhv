package actions

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import play.api.mvc._

@Singleton
class AdminAction @Inject() (
  implicit val ec: ExecutionContext,
) extends AdminActionT {

  override def filter[A](request: AuthenticatedRequest[A]): Future[Option[Result]] = {
    val result = request.user match {
      case u if u.username == "admin" => None
      case _ => Some(Results.Forbidden)
    }
    Future.successful(result)
  }
}