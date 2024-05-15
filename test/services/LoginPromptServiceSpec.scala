package services

import models.{LoginPrompt, LoginPromptRepository, UserPromptView, UserPromptViewRepository}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

import java.time.Instant
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class LoginPromptServiceSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with ScalaFutures {

  "LoginPromptService" should {

    "return a valid random login prompt for a user excluding quiet prompts" in {
      val loginPromptRepo = mock(classOf[LoginPromptRepository])
      val userPromptViewRepo = mock(classOf[UserPromptViewRepository])
      val service = new LoginPromptService(loginPromptRepo, userPromptViewRepo)

      val userId = 123L
      val now = Instant.now
      val prompt1 = LoginPrompt(1, "Question1?", "Answer1", None)
      val prompt2 = LoginPrompt(2, "Question2?", "Answer2", None)
      val quietUntil = now.plusSeconds(3600) // 1 hour later
      val userPromptView = UserPromptView(userId, 1, quietUntil)

      when(userPromptViewRepo.findByUser(userId)).thenReturn(Future.successful(Seq(userPromptView)))
      when(loginPromptRepo.list()).thenReturn(Future.successful(Seq(prompt1, prompt2)))

      // Verifications and assertions
      for (i <- 1 to 5) {
        val result = service.getEligiblePromptForUser(userId).futureValue
        result mustBe defined
        result.get.id mustNot be(1) // Prompt 1 should be excluded
        result.get.id mustBe 2
      }
    }

    "return None if all available prompts are in the quiet period" in {
      val loginPromptRepo = mock(classOf[LoginPromptRepository])
      val userPromptViewRepo = mock(classOf[UserPromptViewRepository])
      val service = new LoginPromptService(loginPromptRepo, userPromptViewRepo)

      val userId = 123L
      val now = Instant.now
      val prompt1 = LoginPrompt(1, "Question1?", "Answer1", None)
      val quietUntil = now.plusSeconds(7200) // 2 hours later
      val userPromptView = UserPromptView(userId, 1, quietUntil)

      when(userPromptViewRepo.findByUser(userId)).thenReturn(Future.successful(Seq(userPromptView)))
      when(loginPromptRepo.list()).thenReturn(Future.successful(Seq(prompt1)))

      // Verifications and assertions
      val result = service.getEligiblePromptForUser(userId).futureValue
      result mustBe None
    }

    "update last shown when a prompt is selected" in {
      val loginPromptRepo = mock(classOf[LoginPromptRepository])
      val userPromptViewRepo = mock(classOf[UserPromptViewRepository])
      val service = new LoginPromptService(loginPromptRepo, userPromptViewRepo)

      val userId = 123L
      val promptId = 2L
      val quietPeriod = Some(3600) // 1 hour in seconds
      val prompt = LoginPrompt(promptId, "Question?", "Answer", quietPeriod)

      when(loginPromptRepo.list()).thenReturn(Future.successful(Seq(prompt)))
      when(userPromptViewRepo.findByUser(userId)).thenReturn(Future.successful(Seq.empty))
      when(userPromptViewRepo.upsert(any[UserPromptView])).thenReturn(Future.successful(true))

      // Verifications and assertions
      val result = service.getEligiblePromptForUser(userId).futureValue
      result mustBe defined
      result.get.id mustBe promptId
      verify(userPromptViewRepo).upsert(any[UserPromptView]) // Ensure we update the user prompt view
    }
  }
}
