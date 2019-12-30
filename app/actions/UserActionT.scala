package actions

import play.api.mvc.{ActionBuilder, ActionRefiner, AnyContent, Request}

trait UserActionT extends ActionBuilder[UserRequest, AnyContent]
  with ActionRefiner[Request, UserRequest]
