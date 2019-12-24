package controllers

import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import com.google.inject.Inject
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, DiscardingCookie, MessagesControllerComponents}
import repos.UserRepo
import utils.GlobalKeys
import cats.instances.future._
import actions.SecuredAction
import play.filters.csrf.CSRF

@Singleton
class AuthenticationController @Inject() (
  userRepo: UserRepo,
  securedAction: SecuredAction,
  cc: MessagesControllerComponents,
  )(implicit ec: ExecutionContext
  ) extends AbstractController(cc) {

  def login = Action.async(parse.json[User]) {  implicit request =>
    val logUser = request.body
    userRepo.userByNameAndPassword(logUser.username, logUser.password)
      .map { user =>
        Ok.withSession(GlobalKeys.SESSION_USER_ID_KEY -> user.id.toString)
      }.getOrElse(NotFound)
  }

  def logout = securedAction.async {  implicit request =>
    Future(Ok.withNewSession)
  }

}
