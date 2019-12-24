package controllers.responses

import scala.concurrent.Future

import play.api.libs.json.Json
import play.api.mvc.Results.Forbidden
import play.api.mvc._
import play.filters.csrf.CSRF.ErrorHandler

class CSRFFilterError extends ErrorHandler{
  def handle(req: RequestHeader, msg: String): Future[Result] = {
    val result = Forbidden(Json.toJson(msg))
    Future.successful(result)
  }
}
