package controllers

import models.{LoginPrompt, LoginPromptRepository}
import org.mockito.Mock
import org.mockito.Mockito._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import services.LoginPromptService

class ApiControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "ApiController" should {

    "return a random login prompt as JSON when available" in {
      val mockRepo = mock(classOf[LoginPromptRepository])
      val mockService = mock(classOf[LoginPromptService])
      val controllerComponents = stubControllerComponents()

      // Mocking the data returned by the repository
      val samplePrompt =
        LoginPrompt(1L, "sample caption", "sample url", Some(10))
      when(mockRepo.getRandom())
        .thenReturn(Future.successful(Some(samplePrompt)))

      val controller =
        new ApiController(mockRepo, mockService, controllerComponents)

      val result = controller.getRandomLoginPrompt().apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(samplePrompt)
    }

    "return a 404 when no login prompt is available" in {
      val mockRepo = mock(classOf[LoginPromptRepository])
      val mockService = mock(classOf[LoginPromptService])
      val controllerComponents = stubControllerComponents()

      // Mocking the data returned by the repository
      when(mockRepo.getRandom())
        .thenReturn(Future.successful(None))

      val controller =
        new ApiController(mockRepo, mockService, controllerComponents)

      val result = controller.getRandomLoginPrompt().apply(FakeRequest())

      status(result) mustBe NOT_FOUND
      contentType(result) mustBe None
      contentAsString(result) mustBe ""
    }

    "gracefully handle internal failures" in {
      val mockRepo = mock(classOf[LoginPromptRepository])
      val mockService = mock(classOf[LoginPromptService])
      val controllerComponents = stubControllerComponents()

      when(mockRepo.getRandom())
        .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

      val controller =
        new ApiController(mockRepo, mockService, controllerComponents)

      val result = controller.getRandomLoginPrompt().apply(FakeRequest())

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe None
      contentAsString(result) mustBe ""
    }

    "return a random login prompt for a user as JSON when available" in {
      val userId = 123L
      val mockRepo = mock(classOf[LoginPromptRepository])
      val mockService = mock(classOf[LoginPromptService])
      val controllerComponents = stubControllerComponents()
      val samplePromptForUser =
        LoginPrompt(1L, "sample caption", "sample url", Some(10))

      when(mockService.getEligiblePromptForUser(userId))
        .thenReturn(Future.successful(Some(samplePromptForUser)))

      val controller =
        new ApiController(mockRepo, mockService, controllerComponents)

      val result =
        controller.getRandomLoginPromptForUser(userId).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(samplePromptForUser)
    }

    "return a 404 when no login prompt is available for a user" in {
      val userId = 123L
      val mockRepo = mock(classOf[LoginPromptRepository])
      val mockService = mock(classOf[LoginPromptService])
      val controllerComponents = stubControllerComponents()

      when(mockService.getEligiblePromptForUser(userId))
        .thenReturn(Future.successful(None))

      val controller =
        new ApiController(mockRepo, mockService, controllerComponents)

      val result =
        controller.getRandomLoginPromptForUser(userId).apply(FakeRequest())

      status(result) mustBe NOT_FOUND
      contentType(result) mustBe None
      contentAsString(result) mustBe ""
    }

    "gracefully handle internal failures for a user" in {
      val userId = 123L
      val mockRepo = mock(classOf[LoginPromptRepository])
      val mockService = mock(classOf[LoginPromptService])
      val controllerComponents = stubControllerComponents()

      when(mockService.getEligiblePromptForUser(userId))
        .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

      val controller =
        new ApiController(mockRepo, mockService, controllerComponents)

      val result =
        controller.getRandomLoginPromptForUser(userId).apply(FakeRequest())

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe None
      contentAsString(result) mustBe ""
    }
  }

}
