package services

import java.time.{Instant, LocalDateTime, ZoneId}
import java.time.temporal.TemporalUnit
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

import javax.inject.{Inject, Singleton}
import models._
import org.joda.time.Hours
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

class LoginPromptService @Inject()(
    loginPromptRepo: LoginPromptRepository,
    userPromptViewRepo: UserPromptViewRepository
  )(implicit ec: ExecutionContext) {

  def getEligiblePromptForUser(userId: Long): Future[Option[LoginPrompt]] = {
    val now = Instant.now()

    // declared as vals, so they run in parallel
    val eventualPromptViews = userPromptViewRepo.findByUser(userId)
    val eventualLoginPrompts = loginPromptRepo.list()

    for {
      promptViews <- eventualPromptViews
      loginPrompts <- eventualLoginPrompts
      quietPrompts = promptViews.collect{
        case pv if pv.quietUntil.isAfter(now) => pv.promptId
      }
      validPrompts = loginPrompts.filterNot(lp => quietPrompts.contains(lp.id))
      maybeSelected = validPrompts match {
        case Nil => None
        case _ => Random.shuffle(validPrompts).headOption
      }
      _ <- maybeSelected.fold(Future.unit)(prompt => updateLastShown(userId, prompt.id, prompt.quietPeriodSec))
    } yield maybeSelected
  }

  private def updateLastShown(userId: Long, promptId: Long, quietPeriod: Option[Int]): Future[Unit] = {
    quietPeriod.fold(Future.unit) { quietPeriod =>
      val quietUntil = Instant.now().plusSeconds(quietPeriod)
      val upv = UserPromptView(userId, promptId, quietUntil)
      userPromptViewRepo.upsert(upv).map(_ => ())
    }
  }
}
