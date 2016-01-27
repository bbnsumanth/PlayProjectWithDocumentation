package models

import org.h2.index.HashIndex
import play.api.libs.json.{Writes, Format, Json}
import play.api.libs.ws.{WSRequest, WS}
import play.api.Play.current
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class ShortUrl(hash: String, fullUrl: String)

object ShortUrl {

  implicit val shortUrlFmt: Format[ShortUrl] = Json.format[ShortUrl]

  def lookup(hash: String)(implicit ec: ExecutionContext): Future[Option[ShortUrl]] = {
    //todo:read the mapping from db and return ShortUrl obj
    val fullurl: Future[Option[String]] = ShorturlDAO.findByHash(hash)
    fullurl.map((result: Option[String]) => {
      result.map{ (fullurl: String) =>
        ShortUrl(hash,fullurl)
      }
    })
  }

  def shorten(fullUrl: String)(implicit ec: ExecutionContext): Future[ShortUrl] = {
    //todo:compute a unique hash,done
    nextUniqueId.map{nextId =>
      hashId(nextId)
    }.flatMap{hash =>
      val insertFuture = ShorturlDAO.saveMapping(hash,fullUrl)
      insertFuture.map{ _ =>
        ShortUrl(hash,fullUrl)
      }
    }
  }

  //val countIoServiceUrl: String = "http://count.io/vb/urlshortner/hash+"
  //val countioRequestHolder: WSRequest = WS.url(countIoServiceUrl)

  private def nextUniqueId(implicit ex: ExecutionContext): Future[Int] = {
   /*
    countioRequestHolder.post("").map{response =>
     (response.json \ "count").as[Int]
   }
   */
    val r = new Random().nextInt(30)
    Future.successful(r)
  }

  private val hashIndex: String = "abcdefghABCDEFGH123456"
  private def hashId(id: Int,acc: String = ""): String = {
    //val hashInd = hashIndex + url
    id match {
      case 0 => acc
      case _ => hashId(id / hashIndex.length, acc + hashIndex.charAt(id % hashIndex.length))
    }
  }


}