package models

import scala.concurrent.{ExecutionContext, Future}

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

/**
 * This class manages a persistent collection of LoginPrompt objects,
 * @param dbConfigProvider The Slick database provider.
 * @param ec The execution context for chained futures.
 */
@Singleton
class LoginPromptRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class LoginPromptTable(tag: Tag) extends Table[LoginPrompt](tag, "LOGIN_PROMPT") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def caption = column[String]("CAPTION")

    def imageUrl =column[String]("IMAGE_URL")

    def * = (id, caption, imageUrl) <> ((LoginPrompt.apply _).tupled, LoginPrompt.unapply)
  }

  private val loginPrompts = TableQuery[LoginPromptTable]

  /**
   * Creates a new login prompt with the given properties.
   */
  def create(caption: String, imageUrl: String): Future[LoginPrompt] = db.run {
    (loginPrompts.map(lp => (lp.caption, lp.imageUrl))
      returning loginPrompts.map(_.id)
      into ((values, id) => LoginPrompt(id, values._1, values._2))
      ) += (caption, imageUrl)
  }

  /**
   * Returns the complete list of login prompts.
   */
  def list(): Future[Seq[LoginPrompt]] = db.run {
    loginPrompts.result
  }

  /**
   * Finds a login prompt by id.
   */
  def findById(id: Long): Future[Option[LoginPrompt]] = db.run {
    loginPrompts.filter(_.id === id).result.headOption
  }

  /**
   * Updates the login prompt by id.
   */
  def update(loginPrompt: LoginPrompt): Future[Boolean] = db.run {
    loginPrompts
      .filter(_.id === loginPrompt.id)
      .map(lp => (lp.caption, lp.imageUrl))
      .update(loginPrompt.caption, loginPrompt.imageUrl)
  }.map(_ == 1)

  /**
   * Deletes the login prompt with the given unique id.
   */
  def delete(id: Long): Future[Boolean] = db.run {
    loginPrompts.filter(_.id === id).delete
  }.map(_ == 1)
}
