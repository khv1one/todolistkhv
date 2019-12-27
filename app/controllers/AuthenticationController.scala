package controllers

import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import cats.instances.future._
import actions.{UserAction, UserActionT}
import com.google.inject.Inject
import models.User
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import play.filters.csrf.CSRFAddToken
import repos.UserRepo
import utils.GlobalKeys

@Singleton
class AuthenticationController @Inject() (
  userRepo: UserRepo,
  userAction: UserActionT,
  addToken: CSRFAddToken,
  cc: MessagesControllerComponents,
  )(implicit ec: ExecutionContext
  ) extends AbstractController(cc) {

  def login(log: String, pass: String) = addToken( Action.async {  implicit request =>
    userRepo.userByNameAndPassword(log, pass)
      .map { user =>
        Ok.withSession(GlobalKeys.SESSION_USER_NAME_KEY -> user.username)
      }.getOrElse(NotFound)
  })

  def logout = userAction.async {  implicit request =>
    Future(Ok.withNewSession)
  }

}
