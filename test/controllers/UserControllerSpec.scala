package controllers

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import cats.data.OptionT
import actions.{AdminActionMock, UserActionMock}
import models.User
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{BodyParsers, Request, Results}
import play.api.test.Helpers._
import play.api.test._
import repos.UserRepo

class UserControllerSpec
  extends PlaySpec with Results with GuiceOneAppPerSuite with MockitoSugar with MustMatchers {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit val mat: ActorSystem = ActorSystem()
  val bp = new BodyParsers.Default

  val smcc = stubMessagesControllerComponents()

  def beforeAll(): Unit = Play.start(app)
  def afterAll(): Unit = Play.stop(app)

  val userRepo: UserRepo = mock[UserRepo]
  val sessionUser: User = User(777, "me", "pass")
  val sessionAdmin: User = User(0, "admin", "admin")
  val userAction = new UserActionMock(Option(sessionUser))
  val adminAction = new AdminActionMock(Option(sessionAdmin))

  val controller = new UserController(userRepo, userAction, adminAction, smcc)

  "users" should {
    val url = controllers.routes.UserController.users().url
    val request = FakeRequest(GET, url)

    "not empty response from repo" in {
      val users = Seq(User(0, "user1", "pass1"), User(1, "admin", "admin"))

      when( userRepo.users ).thenReturn(Future(users))
      val method = controller.users.apply(request)
      status(method) mustBe OK
      contentAsJson(method) mustBe Json.toJson(users)
    }

    "empty response from repo" in {
      when( userRepo.users ).thenReturn(Future(Nil))
      val method = controller.users.apply(request)
      status(method) mustBe OK
      contentAsJson(method) mustBe Json.toJson(Seq[User]())
    }

  }

  "me" should {
    val url = controllers.routes.UserController.me().url
    val request = FakeRequest(GET, url)
    when( userRepo.userById(anyLong()) ).thenReturn(OptionT(Future(Option(sessionUser))))
    val method = controller.me.apply(request)

    "read data from request" in {
      verify(userRepo).userById(sessionUser.id)
    }

    "transfer data from repo to Result" in {
      status(method) mustBe OK
      contentAsJson(method) mustBe Json.toJson(sessionUser)
    }

    "user not found in repo" in {
      when( userRepo.userById(anyLong()) ).thenReturn(OptionT(Future(Option.empty)))
      val method = controller.me.apply(request)
      status(method) mustBe NOT_FOUND
    }
  }

  "userById(id: Long)" should {
    val requestId = 5
    val url = controllers.routes.UserController.userById(requestId).url
    val repoResponseUser = User(requestId, "user", "pass")
    val request = FakeRequest(GET, url)

    when( userRepo.userById(anyLong()) ).thenReturn(OptionT(Future(Option(repoResponseUser))))
    val method = controller.userById(requestId).apply(request)

    "read data from request" in {
      verify(userRepo).userById(requestId)
    }

    "transfer data from repo to Result" in {
      status(method) mustBe OK
      contentAsJson(method) mustBe Json.toJson(repoResponseUser)
    }

    "user not found in repo" in {
      when( userRepo.userById(anyLong()) ).thenReturn(OptionT(Future(Option.empty)))
      val method = controller.me.apply(request)
      status(method) mustBe NOT_FOUND
    }
  }

  "userByName(name: String)" should {
    val requestName = "Name"
    val url = controllers.routes.UserController.userByName(requestName).url
    val repoResponseUser = User(0, requestName, "pass")
    val request = FakeRequest(GET, url)

    when( userRepo.userByName(anyString()) ).thenReturn(OptionT(Future(Option(repoResponseUser))))
    val method = controller.userByName(requestName).apply(request)

    "read data from request" in {
      verify(userRepo).userByName(requestName)
    }

    "transfer data from repo to Result" in {
      status(method) mustBe OK
      contentAsJson(method) mustBe Json.toJson(repoResponseUser)
    }

    "user not found in repo" in {
      when( userRepo.userByName(anyString()) ).thenReturn(OptionT(Future(Option.empty)))
      val method = controller.me.apply(request)
      status(method) mustBe NOT_FOUND
    }
  }

  "delete(id: Long)" should {
    val deleteId = 5
    val url = controllers.routes.UserController.delete(deleteId).url
    val request = FakeRequest(GET, url)

    "user found" in {
      when( userRepo.delete(anyLong()) ).thenReturn(Future(1))
      val method = controller.delete(deleteId).apply(request)
      status(method) mustBe OK
    }

    "user not found" in {
      when( userRepo.delete(anyLong()) ).thenReturn(Future(0))
      val method = controller.delete(deleteId).apply(request)
      status(method) mustBe NOT_FOUND
    }

//    "db not response" in {
//      when( userRepo.delete(anyLong()) ).thenReturn(Future.never)
//      val method = controller.delete(deleteId).apply(request)
//      status(method) mustBe SERVICE_UNAVAILABLE
//    }
  }

  "update" should {
    val url = controllers.routes.UserController.update().url
    val request: Request[JsValue] = FakeRequest(PUT, url)
      .withHeaders(CONTENT_TYPE -> JSON)
      .withBody(Json.toJson(sessionUser))

    "user found" in {
      when( userRepo.update(any[User]) ).thenReturn(Future(1))
      val method = call(controller.update, request)
      status(method) mustBe OK
    }

    "user not found" in {
      when( userRepo.update(any[User]) ).thenReturn(Future(0))
      val method = call(controller.update, request)
      status(method) mustBe NOT_FOUND
    }

    "user update not his data" in {
      val strangerUser = User(1, "newname", "newpass")
      val request = FakeRequest(PUT, url)
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(Json.toJson(strangerUser))
      val method = call(controller.update, request)
      status(method) mustBe NOT_FOUND
    }

    "corrupt json" in {
      val request = FakeRequest(GET, url)
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(Json.obj(
          "id"    -> 5,
          "usergames" -> "error",
          "password" -> "qwerty"
        ))

      val method = call(controller.update, request)
      status(method) mustBe BAD_REQUEST
    }
  }

  "addUser" should {
    val url = controllers.routes.UserController.addUser().url
    val nUser = User(777, "newUser", "newPass")
    val request: Request[JsValue] = FakeRequest(POST, url)
      .withHeaders(CONTENT_TYPE -> JSON)
      .withBody(Json.toJson(nUser))

    "add" in {
      when( userRepo.add(any[User]) ).thenReturn(Future(1))
      val method = call(controller.addUser, request)
      status(method) mustBe CREATED
    }

    "read data from request" in {
      verify(userRepo).add(nUser)
    }

    "not add" in {
      when( userRepo.add(any[User]) ).thenReturn(Future(0)) // что нужно возвращать, чтобы отрабатывал case recover в контроллере
      val method = call(controller.addUser, request)
      status(method) mustBe BAD_REQUEST
    }

    "corrupt json" in {
      val request = FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(Json.obj(
          "id"    -> 5,
          "usergames" -> "error",
          "password" -> "qwerty"
        ))

      val method = call(controller.addUser, request)
      status(method) mustBe BAD_REQUEST
    }
  }
}
