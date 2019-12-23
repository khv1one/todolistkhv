package controllers

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

import akka.actor.Status.Success
import cats.data.{EitherT, Nested, OptionT}
import com.google.inject.Inject
import models.{Task, User}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents, Result}
import repos.{TaskRepo, UserRepo}
import cats.instances.future._
import actions.SecuredAction

class TaskController @Inject() (
  taskRepo: TaskRepo,
  userRepo: UserRepo,
  securedAction: SecuredAction,
  cc: MessagesControllerComponents
  ) (implicit ex: ExecutionContext
  ) extends AbstractController (cc) {

  def addTask = Action.async(parse.json[Task]) { implicit request =>
    taskRepo.add(request.body)
      .map ( _ => Created )
      .recover { case _ => BadRequest }
  }

  def tasks = securedAction.async { implicit request =>
    taskRepo.tasks.map( tasks => Ok(Json.toJson(tasks)) )
  }

  def taskById(id: Long) = Action.async { implicit request =>
    taskRepo.taskById(id)
      .map( task => Ok(Json.toJson(task)) )
      .getOrElse(NotFound)
  }

  def tasksByUserId(id: Long) = Action.async { implicit request =>
    taskRepo.tasksByUserId(id).map( tasks => Ok(Json.toJson(tasks)) )
  }

  def tasksByUserName(name: String) = Action.async { implicit request =>
    val tasks = for {
      user <- userRepo.userByName(name)
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(user.id))
    } yield Ok(Json.toJson(tasks))

    tasks.getOrElse(NotFound)
  }

  def update = Action.async(parse.json[Task]) { implicit request =>
    taskRepo.update(request.body)
      .map( result => if (result != 0) Ok else NotFound )
      .recover{ case _ => ServiceUnavailable  }
  }

  def delete(id: Long) = Action.async { implicit request =>
    taskRepo.delete(id)
      .map( result => if (result != 0) Ok else NotFound)
      .recover{ case _ => ServiceUnavailable}
  }

}
