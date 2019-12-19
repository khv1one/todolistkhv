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

    val user = request.body.validate[User]
      user.fold (
        error =>
          BadRequest(Json.obj("status" -> "Invalid user json structure", "message" -> JsError.toJson(error))),
        usr => {
          val res = userRepo.addUser(usr)
          Ok(Json.obj("status" -> "OK", "message" -> ("User '" + usr.username + "' saved.")))
      })
  }



}
