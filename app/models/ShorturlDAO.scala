package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import scala.concurrent.{Future, ExecutionContext}

object ShorturlDAO {
  def saveMapping(hash: String, fullUrl: String)(implicit ex: ExecutionContext): Future[Unit] = Future{
    DB.withConnection { implicit c =>
      val sql = SQL("Insert INTO shorturls (short,fullUrl) VALUE ({short},{fullurl})")
        .on("short" -> hash, "fullurl" -> fullUrl)
      sql.executeInsert()
    }
  }

  def findByHash(hash:String)(implicit ex: ExecutionContext):Future[Option[String]] = Future{
    DB.withConnection { implicit c =>
      val sql = SQL("SELECT fullurl FROM shorturls WHERE short = {short} LIMIT 1")
        .on("short" -> hash)
      sql().headOption.map((row: Row) => row[String]("fullurl"))
    }
  }

}
