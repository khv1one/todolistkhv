package controllers

import scala.concurrent.ExecutionContext

import actions.{AdminActionMock, UserActionMock}
import models.User
import org.specs2.mock.Mockito
import play.api.{Application, Play}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._
import repos.UserRepo

class UserControllerSpec (
  implicit ec: ExecutionContext
) extends PlaySpecification with Results with Mockito {

  val app: Application = new GuiceApplicationBuilder().build()
  def beforeAll = Play.start(app)
  def afterAll  = Play.stop(app)


  val userRepo = mock[UserRepo]
  val userAction = new UserActionMock(false)
  val adminAction = new AdminActionMock(true, User(0, "admin", "admin"))
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
