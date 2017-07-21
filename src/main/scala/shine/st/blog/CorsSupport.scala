package shine.st.blog

import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{HttpOrigin, _}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import com.typesafe.config.ConfigFactory

/**
  * Created by shinest on 21/06/2017.
  */
trait CorsSupport {
  lazy val allowedOrigin = {
    val config = ConfigFactory.load()
    val sAllowedOrigin = config.getString("cors.allowed-origin")
    HttpOrigin(sAllowedOrigin)
  }

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`(allowedOrigin),
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
    )
  }

  //this handles preflight OPTIONS requests.
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  def corsHandler(route: Route) = (encodeResponseWith(Gzip) & addAccessControlHeaders) {
    preflightRequestHandler ~ route
  }
}
