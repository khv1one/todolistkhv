package controllers

import scala.concurrent.ExecutionContext

import cats.data.OptionT
import cats.instances.future._
import actions.{AdminActionT, AuthenticatedActionT}
import com.google.inject.Inject
import models.Task
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repos.{TaskRepo, UserRepo}

class TaskController @Inject() (
  taskRepo: TaskRepo,
  userRepo: UserRepo,
  authAction: AuthenticatedActionT,
  adminAction: AdminActionT,
  cc: ControllerComponents
)(
  implicit ex: ExecutionContext
) extends AbstractController (cc) {

  def addTask(): Action[Task] = authAction.async(parse.json[Task]) { implicit request =>
    taskRepo.add(request.body)
      .map( _ => Created)
      .recover { case _ => BadRequest }
  }

  def tasks: Action[AnyContent] = authAction.async { implicit request =>
    taskRepo.tasksByUserId(request.user.id).map{ tasks => Ok(Json.toJson(tasks)) }
  }

  def update: Action[Task] = authAction.async(parse.json[Task]) { implicit request =>

    val task = for {
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(request.user.id))
      task <- OptionT(taskRepo.taskById(request.body.id)) if tasks.contains(task)
    } yield task

    task.flatMap { _ =>
      OptionT.liftF(taskRepo.update(request.body)
        .map( result => if (result != 0) Ok else NotFound)
        .recover{ case _ => ServiceUnavailable})
    }.getOrElse(NotFound)

  }

  def delete(id: Long): Action[AnyContent] = authAction.async { implicit request =>

    val task = for {
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(request.user.id))
      task <- OptionT(taskRepo.taskById(id)) if tasks.contains(task)
    } yield task

    task.flatMap { task =>
      OptionT.liftF(taskRepo.delete(task.id)
        .map( result => if (result != 0) Ok else NotFound)
        .recover{ case _ => ServiceUnavailable})
    }.getOrElse(NotFound)

  }

  def allTasks: Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    taskRepo.tasks.map{ tasks => Ok(Json.toJson(tasks)) }
  }

  def tasksByUserId(id: Long): Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    taskRepo.tasksByUserId(id).map{ tasks => Ok(Json.toJson(tasks)) }
  }

  def tasksByUserName(name: String): Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    val tasks = for {
      user <- OptionT(userRepo.userByName(name))
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(user.id))
    } yield Ok(Json.toJson(tasks))

    tasks.getOrElse(NotFound)
  }

  def taskById(id: Long): Action[AnyContent] = (authAction andThen adminAction).async { implicit request =>
    OptionT(taskRepo.taskById(id))
      .map{ task => Ok(Json.toJson(task)) }
      .getOrElse(NotFound)
  }

}
