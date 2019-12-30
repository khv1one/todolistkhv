package actions

import play.api.mvc.{ActionBuilder, AnyContent}

trait AdminActionT extends ActionBuilder[UserRequest, AnyContent]
