package controllers

import scala.concurrent.ExecutionContext

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{Result, Results}

class TestSpec (
  implicit ec: ExecutionContext
) extends PlaySpec with Results with GuiceOneAppPerSuite {

}
