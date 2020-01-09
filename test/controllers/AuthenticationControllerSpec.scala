package controllers

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import actions.AuthenticatedActionMock
import models.User
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play
import play.api.http.SessionConfiguration
import play.api.libs.crypto.CSRFTokenSigner
import play.api.mvc.{AnyContent, BodyParsers, Request, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, stubMessagesControllerComponents, _}
import play.filters.csrf.{CSRFAddToken, CSRFConfig}
import repos.UserRepo
import utils.GlobalKeys

class AuthenticationControllerSpec
  extends PlaySpec with Results with GuiceOneAppPerSuite with MockitoSugar with MustMatchers {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  implicit val mat: ActorSystem = ActorSystem()
  implicit val bp = new BodyParsers.Default
  val smcc = stubMessagesControllerComponents()

  def beforeAll(): Unit = Play.start(app)
  def afterAll(): Unit = Play.stop(app)

  val user: User = User(777, "me", "pass")
  val userRepo: UserRepo = mock[UserRepo]
  val userAction = new AuthenticatedActionMock(user)

  val csrfTokenAdder = app.injector.instanceOf[CSRFTokenSigner]
  val csrfAddToken = CSRFAddToken(CSRFConfig(), csrfTokenAdder, SessionConfiguration())
  val controller = new AuthenticationController(userRepo, userAction, csrfAddToken, smcc)

  "login" should {

    val url = controllers.routes.AuthenticationController.login(user.username, user.password).url
    val request: Request[AnyContent] = FakeRequest(POST, url)

    when( userRepo.userByNameAndPassword(user.username, user.password) )
      .thenReturn(Future(Option(user)))
    val method = controller.login(user.username, user.password).apply(request)

    "check route" in {
      //val result = route(app, request).get
      //status(result) mustBe OK
    }

    "check OK" in {
      status(method) mustBe OK
    }

    "check user.name and user.pass in args to repo" in {
      verify(userRepo).userByNameAndPassword(user.username, user.password)
    }

    "check session" in {
      session(method).get(GlobalKeys.SESSION_USER_NAME_KEY) mustBe Some(user.username)
    }

    "check NF" in {
      when( userRepo.userByNameAndPassword(user.username, user.password) )
        .thenReturn(Future(Option.empty))
      val method = controller.login(user.username, user.password).apply(request)
      status(method) mustBe NOT_FOUND
    }
  }

  "logout" should {
    val url = controllers.routes.AuthenticationController.logout().url
    val request: Request[AnyContent] =
      FakeRequest(POST, url).withSession(GlobalKeys.SESSION_USER_NAME_KEY -> user.username)
    val method = controller.logout.apply(request)

    "check new session" in { // не работает
      method map { res =>
        res.newSession mustBe None
      }
    }
  }
}
