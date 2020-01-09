package actions

import play.api.mvc._

trait AdminActionT extends ActionFilter[AuthenticatedRequest]