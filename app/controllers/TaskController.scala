package controllers

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

import akka.actor.Status.Success
import com.google.inject.Inject
import models.Task
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}
import repos.{TaskRepo, UserRepo}

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

  def getTasks = Action.async { implicit request =>
    taskRepo.tasks.map { tasks =>
      Ok(Json.toJson(tasks))
    }
  }

  def getTasksByUserId(id: Long) = Action.async { implicit request =>
    taskRepo.tasksByUserId(id).map { tasks =>
      Ok(Json.toJson(tasks))
    }
  }

  def getTasksByUserName(name: String): Action[AnyContent] = Action.async { implicit request =>
    userRepo.userByName(name).flatMap {
      case Some(u) =>
        taskRepo.tasksByUserId(u.id).map { tasks =>
          Ok(Json.toJson(tasks))
        }.recover { case _ =>
          ServiceUnavailable
        }
      case None => Future(ServiceUnavailable)
    }.recover { case _ =>
      ServiceUnavailable
    }
  }

}
