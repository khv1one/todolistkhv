package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo

import cats.data._
import cats.instances.future._

class UserController @Inject() (
  userRepo: UserRepo,
  cc: MessagesControllerComponents,
  ) (implicit ec: ExecutionContext
  ) extends AbstractController(cc) {


  def addUser = Action.async(parse.json[User]) { implicit request =>
    val user = request.body
    userRepo.add(user)
      .map { _ =>
        Ok(Json.obj("status" -> "OK", "message" -> (s"User '${user.username}' saved.")))
      }
      .recover { case _ =>
        ServiceUnavailable
      }
  }

  def users = Action.async { implicit request =>
    userRepo.users.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def userById(id: Long) = Action.async { implicit request =>
    userRepo.userById(id)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(ServiceUnavailable)
  }

  def userByName(name: String) = Action.async { implicit request =>
    userRepo.userByName(name)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(ServiceUnavailable)
  }
}
