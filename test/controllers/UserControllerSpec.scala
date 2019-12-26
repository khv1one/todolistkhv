package controllers

import scala.concurrent.{ExecutionContext, Future}

import actions.{AdminActionMock, UserActionMock}
import models.User
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._
import repos.UserRepo

class UserControllerSpec extends PlaySpec with Results with GuiceOneAppPerSuite with MockitoSugar {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  def beforeAll(): Unit = Play.start(app)
  def afterAll(): Unit = Play.stop(app)

  val userRepo = mock[UserRepo]
  val userAction = new UserActionMock()
  val adminAction = new AdminActionMock(Option(User(0, "admin", "admin")))
  val smcc = stubMessagesControllerComponents()
  val controller = new UserController(userRepo, userAction, adminAction, smcc)

  "User Controller" should {

    "Get /users" in {
      val users = Seq(
        User(0, "user1", "pass1"),
        User(1, "admin", "admin")
      )

      when( userRepo.users )
        .thenReturn(Future(users))

      val request = FakeRequest(GET, "/todolist/api/users")
      val method = controller.users.apply(request)

      status(method) mustBe OK
      contentAsJson(method) mustBe Json.toJson(users :+ User(777, "777", "777"))
    }

  }
}
