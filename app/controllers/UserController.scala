package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import cats.data.OptionT
import cats.instances.future._
import actions.{AdminActionT, UserActionT}
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repos.UserRepo

class UserController @Inject() (
  userRepo: UserRepo,
  userAction: UserActionT,
  adminAction: AdminActionT,
  cc: ControllerComponents,
)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  def addUser: Action[User] = Action.async(parse.json[User]) { implicit request =>
    userRepo.add(request.body)
      .map( _ => Created )
      .recover { case _ => BadRequest }
  }

  def me: Action[AnyContent] = userAction.async { implicit request =>
    OptionT(userRepo.userById(request.user.id))
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def update: Action[User] = userAction.async(parse.json[User]) { implicit request =>
    if (request.user.id == request.body.id) {
      userRepo.update(request.body)
        .map( result => if (result != 0) Ok else NotFound ) //вопрос в userRepo.delete
        .recover{ case _ => ServiceUnavailable  }
    } else {
      Future(NotFound)
    }
  }

  def delete(id: Long): Action[AnyContent] = adminAction.async { implicit request =>
    userRepo.delete(id)
      .map( result => if (result != 0) Ok else NotFound)
      .recover{ case _ => ServiceUnavailable}
  }

  def userById(id: Long): Action[AnyContent] = adminAction.async { implicit request =>
    OptionT(userRepo.userById(id))
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def userByName(name: String): Action[AnyContent] = adminAction.async { implicit request =>
    OptionT(userRepo.userByName(name))
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def users: Action[AnyContent] = adminAction.async { implicit request =>
    userRepo.users.map( users => Ok(Json.toJson(users)) )
  }
}
