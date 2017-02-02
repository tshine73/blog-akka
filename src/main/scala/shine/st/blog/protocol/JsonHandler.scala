package shine.st.blog.protocol

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError}

/**
  * Created by shinest on 16/01/2017.
  */
object JsonHandler {

  def handle(t: Throwable) = t match {
    case t: JsonResponse => BadRequest -> t.json
    case e: Exception => InternalServerError -> e
  }


}
