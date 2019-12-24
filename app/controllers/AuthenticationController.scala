package controllers

import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import com.google.inject.Inject
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Cookie, DiscardingCookie, MessagesControllerComponents}
import repos.UserRepo
import utils.GlobalKeys
import cats.instances.future._
import actions.UserAction
import play.filters.csrf.{CSRF, CSRFAddToken}

@Singleton
class AuthenticationController @Inject() (
  userRepo: UserRepo,
  userAction: UserAction,
  addToken: CSRFAddToken,
  cc: MessagesControllerComponents,
  )(implicit ec: ExecutionContext
  ) extends AbstractController(cc) {

  def login = addToken( Action.async(parse.json[User]) {  implicit request =>
    val logUser = request.body
    userRepo.userByNameAndPassword(logUser.username, logUser.password)
      .map { user =>
        Ok.withSession(GlobalKeys.SESSION_USER_ID_KEY -> user.username)
      }.getOrElse(NotFound)
  })

  def logout = userAction.async {  implicit request =>
    Future(Ok.withNewSession)
  }

}
