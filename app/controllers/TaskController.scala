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
import actions.{AdminAction, UserAction}

class TaskController @Inject() (
  taskRepo: TaskRepo,
  userRepo: UserRepo,
  userAction: UserAction,
  adminAction: AdminAction,
  cc: MessagesControllerComponents
) (implicit ex: ExecutionContext
) extends AbstractController (cc) {

  def addTask = userAction.async(parse.json[Task]) { implicit request =>
    taskRepo.add(request.body)
      .map ( _ => Created )
      .recover { case _ => BadRequest }
  }

  def tasks = userAction.async { implicit request =>
    taskRepo.tasksByUserId(request.user.id).map( tasks => Ok(Json.toJson(tasks)) )
  }

  def update = userAction.async(parse.json[Task]) { implicit request =>

    val task = for {
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(request.user.id))
      task <- taskRepo.taskById(request.body.id) if tasks.contains(task)
    } yield task

    task.flatMap { _ =>
      OptionT.liftF(taskRepo.update(request.body)
        .map( result => if (result != 0) Ok else NotFound)
        .recover{ case _ => ServiceUnavailable})
    }.getOrElse(NotFound)

  }

  def delete(id: Long) = userAction.async { implicit request =>

    val task = for {
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(request.user.id))
      task <- taskRepo.taskById(id) if tasks.contains(task)
    } yield task

    task.flatMap { task =>
      OptionT.liftF(taskRepo.delete(task.id)
        .map( result => if (result != 0) Ok else NotFound)
        .recover{ case _ => ServiceUnavailable})
    }.getOrElse(NotFound)

  }

  def allTasks = adminAction.async { implicit request =>
    taskRepo.tasks.map( tasks => Ok(Json.toJson(tasks)) )
  }

  def tasksByUserId(id: Long) = adminAction.async { implicit request =>
    taskRepo.tasksByUserId(id).map( tasks => Ok(Json.toJson(tasks)) )
  }

  def tasksByUserName(name: String) = adminAction.async { implicit request =>
    val tasks = for {
      user <- userRepo.userByName(name)
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(user.id))
    } yield Ok(Json.toJson(tasks))

    tasks.getOrElse(NotFound)
  }

  def taskById(id: Long) = adminAction.async { implicit request =>
    taskRepo.taskById(id)
      .map( task => Ok(Json.toJson(task)) )
      .getOrElse(NotFound)
  }

}
