package controllers

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import cats.data.OptionT
import cats.instances.future._
import actions.{AdminActionT, AuthenticatedActionT}
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repos.UserRepo

class UserController @Inject() (
  userRepo: UserRepo,
  authAction: AuthenticatedActionT,
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

  def me: Action[AnyContent] = authAction.async { implicit request =>
    OptionT(userRepo.userById(request.user.id))
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def update: Action[User] = authAction.async(parse.json[User]) { implicit request =>
    if (request.user.id == request.body.id) {
      userRepo.update(request.body)
        .map( result => if (result != 0) Ok else NotFound ) //вопрос в userRepo.delete
        .recover{ case _ => ServiceUnavailable  }
    } else {
      Future(NotFound)
    }
  }

  def delete(id: Long): Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    userRepo.delete(id)
      .map( result => if (result != 0) Ok else NotFound)
      .recover{ case _ => ServiceUnavailable}
  }

  def userById(id: Long): Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    OptionT(userRepo.userById(id))
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def userByName(name: String): Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    OptionT(userRepo.userByName(name))
      .map( user => Ok(Json.toJson(user)) )
      .getOrElse(NotFound)
  }

  def users: Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    userRepo.users.map( users => Ok(Json.toJson(users)) )
  }
}
