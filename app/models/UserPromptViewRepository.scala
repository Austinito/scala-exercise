
package models

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.PrimaryKey

/**
 * This class manages a persistent collection of UserPromptView objects,
 * @param dbConfigProvider The Slick database provider.
 * @param ec The execution context for chained futures.
 */
@Singleton
class UserPromptViewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UserPromptViewTable(tag: Tag) extends Table[UserPromptView](tag, "USER_PROMPT_VIEW") {
    def userId = column[Long]("USER_ID")
    def promptId = column[Long]("PROMPT_ID")
    def quietUntil = column[Instant]("QUIET_UNTIL")
    def * = (userId, promptId, quietUntil) <> ((UserPromptView.apply _).tupled, UserPromptView.unapply)
    def pk = primaryKey("USER_PROMPT_VIEW_PK", (userId, promptId))
  }

  private val userPromptViews = TableQuery[UserPromptViewTable]

  /**
   * Creates a new user prompt view with the given properties.
   */
  def create(userId: Long, promptId: Long, quietUntil: Instant): Future[UserPromptView] = db.run {
     userPromptViews += UserPromptView(userId, promptId, quietUntil)
  }.map { _ => UserPromptView(userId, promptId, quietUntil) }

  /**
   * Returns the complete list of user prompt views.
   */
  def list(): Future[Seq[UserPromptView]] = db.run {
    userPromptViews.result
  }

  /**
   * Finds all user prompt views for given user.
   */
  def findByUser(userId: Long): Future[Seq[UserPromptView]] = db.run {
    userPromptViews.filter(_.userId === userId).result
  }

  /**
   * Upserts (insert or updates) the user prompt view.
   */
  def upsert(userPromptView: UserPromptView): Future[Boolean] = db.run {
    userPromptViews.insertOrUpdate(userPromptView)
  }.map(_ == 1)

  /**
   * Deletes the user prompt view for given user and prompt.
   */
  def delete(userId: Long, promptId: Long): Future[Boolean] = db.run {
    userPromptViews
      .filter(row => row.userId === userId && row.promptId === promptId)
      .delete
  }.map(_ == 1)

}
