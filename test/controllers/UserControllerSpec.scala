package controllers

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import actions.{AdminAction, UserAction}
import org.checkerframework.checker.units.qual.A
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification.BeforeAfterAll
import play.api.test._
import play.api.test.Helpers._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.Play
import play.api.libs.concurrent.Futures
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, MessagesControllerComponents, Request, Result, Results}
import repos.UserRepo
import org.mockito.Mockito._
import org.mockito.internal.matchers.Matches
import org.specs2.matcher.Matchers
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.runner._

class UserControllerSpec (
  implicit ec: ExecutionContext
) extends PlaySpecification with Results with Mockito {

  val app: Application = new GuiceApplicationBuilder().build()
  def beforeAll = Play.start(app)
  def afterAll  = Play.stop(app)


  val userRepo = mock[UserRepo]
  val userAction = mock[UserAction]
  val adminAction = mock[AdminAction]
  val smcc = stubMessagesControllerComponents()
  val controller = new UserController(userRepo, userAction, adminAction, smcc)
  //val method = controller.me()(FakeRequest())

  "User Controller" should {

    "Get /users" in new WithApplication() {
      val request = FakeRequest(GET, "/todolist/api/users")
      //when( adminAction.invokeBlock(, ) )
      //  .thenReturn(Future(Forbidden))

      val method = controller.users().apply(request)
      status(method) must equalTo(FORBIDDEN)


      //val result = route(app, FakeRequest(GET, "/")).get
      //status(result) must equalTo(BAD_REQUEST)
      //contentType(home) must beSome.which(_ == "text/html")
    }



  }
}
