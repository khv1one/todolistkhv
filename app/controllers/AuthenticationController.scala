package controllers

import scala.concurrent.{ExecutionContext, Future}

import cats.Inject
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo
import utils.GlobalKeys

import cats.instances.future._

class AuthenticationController @Inject()(
  cc: MessagesControllerComponents,
  userRepo: UserRepo,
  )(implicit ec: ExecutionContext
  ) extends AbstractController(cc) {

  def login = Action.async(parse.json[User]) {  implicit request =>

    val logUser = request.body
    userRepo.userByNameAndPassword(logUser.username, logUser.password)
      .map { user =>
        Ok.withSession(GlobalKeys.SESSION_USER_ID_KEY -> user.id.toString)
      }
      .getOrElse(NotFound)
  }

  def logout = Action.async {  implicit request =>
    Future(Ok.withNewSession)
  }

}
