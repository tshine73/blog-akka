package shine.st.blog

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError}
import akka.http.scaladsl.model.headers.HttpOriginRange.*
import akka.http.scaladsl.model.{HttpResponse, headers}
import akka.http.scaladsl.server.Directives.{complete, encodeResponseWith, extractUri, handleExceptions, respondWithHeaders, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import shine.st.blog.api.{CategoryAPI, HomeAPI, PostAPI}
import shine.st.blog.protocol.{JsonResponse, UnknownError}

/**
  * Created by shinest on 16/01/2017.
  */
object MainService extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val commonHeaders = List(headers.`Access-Control-Allow-Origin`(*))

  val exceptionHandler = ExceptionHandler {
    case t: JsonResponse =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(BadRequest, entity = t.json.prettyPrint))
      }

    case e: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        e.printStackTrace
        val err = UnknownError(s"Something wrong...${e.getMessage}")
        complete(HttpResponse(InternalServerError, entity = err.json.prettyPrint))
      }
  }


  /* 如果需要針對不同的uri有各自的option response allow method，可將此段註解和把ServiceRestApi.route 拿掉*/
  //  implicit def rejectionHandler = RejectionHandler.newBuilder().handleAll[MethodRejection] { rejections =>
  //    val methods = rejections map (_.supported)
  //    lazy val names = methods map (_.name) mkString ", "
  //
  //    respondWithHeader(Allow(methods)) {
  //      options {
  //        complete(s"Supported methods : $names.")
  //      } ~
  //        complete(MethodNotAllowed,
  //          s"HTTP method not allowed, supported methods: $names!")
  //    }
  //  }.result()


  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val allRoute: Route = handleExceptions(exceptionHandler) {
    (encodeResponseWith(Gzip) & respondWithHeaders(commonHeaders)) {
      HomeAPI.route ~ PostAPI.route ~ CategoryAPI.route
    }
  }

  Http().bindAndHandle(allRoute, config.getString("http.interface"), config.getInt("http.port"))

}
