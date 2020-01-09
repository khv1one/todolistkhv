package actions

import scala.concurrent.ExecutionContext

import play.api.mvc._

trait AdminActionT extends ActionFilter[AuthenticatedRequest] {
  protected[this] val ec: ExecutionContext
  override protected def executionContext: ExecutionContext = ec
}

