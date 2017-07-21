package shine.st.blog

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, _}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.HttpOriginRange.*
import akka.http.scaladsl.server.Directives.{complete, extractUri, handleExceptions, _}
import akka.http.scaladsl.server.{Route, _}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import shine.st.blog.api.{BackendAPI, CategoryAPI, HomeAPI, PostAPI}
import shine.st.blog.protocol.{JsonResponse, UnknownError}

/**
  * Created by shinest on 16/01/2017.
  */


object MainService extends App with CorsSupport {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val commonHeaders = List(headers.`Access-Control-Allow-Origin`(*))

  val exceptionHandler = ExceptionHandler {
    case t: JsonResponse =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(BadRequest, entity = HttpEntity(ContentType(MediaTypes.`application/json`), t.json.compactPrint)))
      }

    case e: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        e.printStackTrace
        val err = UnknownError(s"Something wrong...${e.getMessage}")
        complete(HttpResponse(InternalServerError, entity = HttpEntity(ContentType(MediaTypes.`application/json`), err.json.compactPrint)))
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

  //  ->header('Access-Control-Allow-Headers', 'authorization,X-Requested-With,Content-Type')
  //  ->header('Access-Control-Allow-Methods', 'POST, GET, PUT, DELETE');

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val frontRoute = HomeAPI.route ~ PostAPI.route ~ CategoryAPI.route
  val backendRoute = pathPrefix("backend") {
    BackendAPI.route
  }

  val apiRoute = frontRoute ~ backendRoute

  val allRoute: Route = corsHandler(handleExceptions(exceptionHandler) {
    apiRoute
  })

  def myRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MissingCookieRejection(cookieName) =>
        complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
      }
      //      .handle { case AuthorizationFailedRejection =>
      //        complete((Forbidden, "You're out of your depth!"))
      //      }
      .handle { case ValidationRejection(msg, _) =>
      complete((InternalServerError, "That wasn't valid! " + msg))
    }
      .handleAll[MethodRejection] { methodRejections =>
      val names = methodRejections.map(_.supported.name)
      complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
    }
      .handleNotFound {
        complete((NotFound, "Not here!"))
      }
      .result()

  Http().bindAndHandle(allRoute, config.getString("http.interface"), config.getInt("http.port"))


}
