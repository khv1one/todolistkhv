package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo

class UserController @Inject() (userRepo: UserRepo,
                                cc: MessagesControllerComponents,
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {


  def addUser = Action.async(parse.json[User]) { implicit request =>
    val user = request.body
    userRepo.add(user)
      .map { _ =>
        Ok(Json.obj("status" -> "OK", "message" -> ("User '" + user.username + "' saved.")))
      }
      .recover { case _ =>
        ServiceUnavailable
      }
  }

  def getAllUsers = Action.async { implicit request =>
    userRepo.users.map { users =>
      Ok(Json.toJson(users))
    }
  }
}
