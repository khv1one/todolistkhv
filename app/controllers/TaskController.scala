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

class TaskController @Inject() (
  taskRepo: TaskRepo,
  userRepo: UserRepo,
  cc: MessagesControllerComponents
  ) (implicit ex: ExecutionContext
  ) extends AbstractController (cc) {

  def addTask = Action.async(parse.json[Task]) { implicit request =>
    taskRepo.add(request.body).map { _ =>
      Ok(Json.obj("status" -> "OK", "message" -> "Task saved."))
    }.recover { case _ =>
      ServiceUnavailable
    }
  }

  def tasks = Action.async { implicit request =>
    taskRepo.tasks.map { tasks =>
      Ok(Json.toJson(tasks))
    }
  }

  def tasksByUserId(id: Long) = Action.async { implicit request =>
    taskRepo.tasksByUserId(id).map { tasks =>
      Ok(Json.toJson(tasks))
    }
  }

  def tasksByUserName(name: String): Action[AnyContent] = Action.async { implicit request =>
    val tasks = for {
      user <- userRepo.userByName(name)
      tasks <- OptionT.liftF(taskRepo.tasksByUserId(user.id))
    } yield tasks

    tasks
      .map( tasks => Ok(Json.toJson(tasks)) )
      .getOrElse(ServiceUnavailable)
  }
}
