package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import cats.instances.future._
import actions.{AdminActionT, UserActionT}
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import repos.UserRepo

class UserController @Inject() (
  userRepo: UserRepo,
  userAction: UserActionT,
  adminAction: AdminActionT,
  cc: MessagesControllerComponents,
) (implicit ec: ExecutionContext
) extends AbstractController(cc) {

  def addUser = Action.async(parse.json[User]) { implicit request =>
    userRepo.add(request.body)
      .map( _ => Created )
      .recover { case _ => BadRequest }
  }

  def me = userAction.async { implicit request =>
    userRepo.userById(request.user.id)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def update = userAction.async(parse.json[User]) { implicit request =>
    if (request.user.id == request.body.id) {
      userRepo.update(request.body)
        .map( result => if (result != 0) Ok else NotFound ) //вопрос в userRepo.delete
        .recover{ case _ => ServiceUnavailable  }
    } else {
      Future(NotFound)
    }
  }

  def delete(id: Long) = adminAction.async { implicit request =>
    userRepo.delete(id)
      .map( result => if (result != 0) Ok else NotFound)
      .recover{ case _ => ServiceUnavailable}
  }

  def userById(id: Long) = adminAction.async { implicit request =>
    userRepo.userById(id)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def userByName(name: String) = adminAction.async { implicit request =>
    userRepo.userByName(name)
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def users = adminAction.async { implicit request =>
    userRepo.users.map( users => Ok(Json.toJson(users)) )
  }
}
