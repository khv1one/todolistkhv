package actions

import scala.concurrent.ExecutionContext

import play.api.mvc.{ActionBuilder, ActionRefiner, AnyContent, BodyParser, BodyParsers, Request}

trait AuthenticatedActionT extends ActionBuilder[AuthenticatedRequest, AnyContent]
  with ActionRefiner[Request, AuthenticatedRequest] {

  protected[this] val bp: BodyParsers.Default
  protected[this] val ec: ExecutionContext

  override def parser: BodyParser[AnyContent] = bp
  override protected def executionContext: ExecutionContext = ec
}
