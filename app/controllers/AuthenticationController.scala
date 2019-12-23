package controllers

import scala.concurrent.{ExecutionContext, Future}

import cats.Inject
import models.User
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo

class AuthenticationController @Inject()(
  cc: MessagesControllerComponents,
  userRepo: UserRepo,
  )(implicit ec: ExecutionContext
  ) extends AbstractController(cc) {

  def login = Action.async(parse.json[User]) {  implicit request =>
    Future(Ok)
  }

  def logout = Action.async {  implicit request =>
    Future(Ok)
  }

}
