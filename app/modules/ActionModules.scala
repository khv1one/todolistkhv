package modules

import actions.{AdminAction, AdminActionT, AuthenticatedAction, AuthenticatedActionT}
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}

class ActionModules extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[AdminActionT].to[AdminAction],
    bind[AuthenticatedActionT].to[AuthenticatedAction]
  )
}