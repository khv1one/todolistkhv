package actions

import models.User
import play.api.mvc.{Request, WrappedRequest}

class AuthenticatedRequest[A](
  val user: User,
  val request: Request[A]
) extends WrappedRequest[A](request)
