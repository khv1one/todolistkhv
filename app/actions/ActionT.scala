package actions

import play.api.mvc.ActionBuilder

trait ActionT[+R[_], B] extends ActionBuilder[R, B]
