package actions

import play.api.mvc.{ActionBuilder, AnyContent}

trait UserActionT extends ActionBuilder[UserRequest, AnyContent]
