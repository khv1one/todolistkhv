package controllers.hendlers

import scala.concurrent.Future

import play.api.libs.json.Json
import play.api.mvc.Results.Forbidden
import play.api.mvc._
import play.filters.csrf.CSRF.ErrorHandler

class CsrfErrorHandler extends ErrorHandler{
  def handle(req: RequestHeader, msg: String): Future[Result] = {
    Future.successful(Forbidden(Json.toJson(msg)))
  }
}
