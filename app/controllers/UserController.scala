package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import models.User
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo

class UserController @Inject() (userRepo: UserRepo,
                                cc: MessagesControllerComponents,
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {


  def addUser = Action(parse.tolerantJson) { implicit request =>
    request.body.validate[User].fold(
      error => BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(error))),
      user => {
        userRepo.addUser(user)
        Ok(Json.obj("status" -> "OK", "message" -> ("User '" + user.username + "' saved.")))
      })
  }
}
