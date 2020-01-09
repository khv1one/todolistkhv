package controllers

import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import cats.data.OptionT
import cats.instances.future._
import actions.AuthenticatedActionT
import com.google.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.filters.csrf.CSRFAddToken
import repos.UserRepo
import utils.GlobalKeys

@Singleton
class AuthenticationController @Inject() (
  userRepo: UserRepo,
  userAction: AuthenticatedActionT,
  addToken: CSRFAddToken,
  cc: ControllerComponents,
)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  def login(log: String, pass: String): Action[AnyContent] = addToken( Action.async {  implicit request =>
    OptionT(userRepo.userByNameAndPassword(log, pass))
      .map { user =>
        Ok.withSession(GlobalKeys.SESSION_USER_NAME_KEY -> user.username)
      }.getOrElse(NotFound)
  })

  def logout: Action[AnyContent] = userAction.async {  implicit request =>
    Future(Ok.withNewSession)
  }

}
