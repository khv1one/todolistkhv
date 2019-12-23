package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo
import actions.SecuredAction

import cats.data._
import cats.instances.future._

class UserController @Inject() (
  userRepo: UserRepo,
  securedAction: SecuredAction,
  cc: MessagesControllerComponents,
  ) (implicit ec: ExecutionContext
  ) extends AbstractController(cc) {


  def addUser = Action.async(parse.json[User]) { implicit request =>
    userRepo.add(request.body)
      .map( _ => Created )
      .recover { case _ => BadRequest }
  }

  def users = securedAction.async { implicit request =>
    userRepo.users.map( users => Ok(Json.toJson(users)) )
  }

  def userById(id: Long) = Action.async { implicit request =>
    userRepo.userById(id)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def userByName(name: String) = Action.async { implicit request =>
    userRepo.userByName(name)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def update = Action.async(parse.json[User]) { implicit request =>
    userRepo.update(request.body)
      .map( result => if (result != 0) Ok else NotFound )
      .recover{ case _ => ServiceUnavailable  }
  }

  def delete(id: Long) = Action.async { implicit request =>
    userRepo.delete(id)
      .map( result => if (result != 0) Ok else NotFound)
      .recover{ case _ => ServiceUnavailable}
  }

}
