package shine.st.blog.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import shine.st.blog.protocol.RESTfulResponse
import shine.st.blog.protocol.doObj.PagingDo
import shine.st.blog.services.PagingService
import shine.st.blog.utils.Memoize

/**
  * Created by shinest on 16/01/2017.
  */
object HomeAPI extends BaseAPI {

  val homeMemo = Memoize.memoize(PagingService.homePaging _)

  override def route: Route = {
    pathPrefix("home") {
      (get & path(IntNumber)) {
        page =>
          complete {
            getHomePosts(page)
          }
      }
    }
  }

  def getHomePosts(page: Int): RESTfulResponse[PagingDo] = {
    val homePosts = if (isExpire(homeMemo(page).queryAt)) {
      println("home posts update...")
      homeMemo.update(page)
    } else
      homeMemo(page)
    success(homePosts)
  }

}
