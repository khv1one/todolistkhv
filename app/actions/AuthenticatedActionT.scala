package actions

import play.api.mvc.{ActionBuilder, ActionRefiner, AnyContent, Request}

trait AuthenticatedActionT extends ActionBuilder[AuthenticatedRequest, AnyContent]
  with ActionRefiner[Request, AuthenticatedRequest]
