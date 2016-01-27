package controllers

import models.ShortUrl.shortUrlFmt
import models.ShortUrl
import play.api.libs.json.Json
import play.api.mvc._
import views.html.index
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Application extends Controller {


  def echo: Action[AnyContent] = Action { (request: Request[AnyContent]) =>
    val result: Result = Ok("Got request [" + request + "]")
    result
  }

  def testingServer(): Action[AnyContent] =Action{
    Ok("server is working,\n valid paths: POST /urls ,GET /:hash \n TODO:create a form to get urls and make a post request")
  }

  def redirect(hash: String): Action[AnyContent] = Action.async {

    val result: Future[Result] = ShortUrl.lookup(hash).map{
      case None => NotFound(s"couldn't find the url for this hash:$hash")
      case Some(shortUrl) => Redirect(shortUrl.fullUrl,301)
    }
    result
  }

  def shorten: Action[AnyContent] = Action.async { request =>
    //parse the url from body
    val fullurl: Option[String] = request.body.asJson.map(js => {
      (js \ "fullurl").as[String]
    } )
    //shorten the url
    val result: Future[Result] = fullurl match {
      case None => Future.successful(BadRequest("Query must be json"))
      case Some(fullurl) => ShortUrl.shorten(fullurl).map((shortUrl: ShortUrl) => {
        Ok(Json.obj("result" -> shortUrl))
        //note for above we should use import play.api.libs.json.Json not import play.libs.Json
      })
    }
    result
  }
}
