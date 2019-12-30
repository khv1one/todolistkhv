package actions

import play.api.mvc.{ActionBuilder, ActionRefiner, AnyContent, Request}

trait AdminActionT extends ActionBuilder[UserRequest, AnyContent]
  with ActionRefiner[Request, UserRequest]
