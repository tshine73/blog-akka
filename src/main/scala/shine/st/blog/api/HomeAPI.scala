package shine.st.blog.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import shine.st.blog.services.HomeService
import shine.st.blog.utils.Memoize

/**
  * Created by shinest on 16/01/2017.
  */
object HomeAPI extends BaseAPI {
  val homeMemo = Memoize.memoize(HomeService.homePaging _)
  val aboutMemo = Memoize.memoize(HomeService.about _)

  override def route: Route = {
    pathPrefix("home") {
      (get & path(IntNumber)) {
        page =>
          complete {
            memoProcess(homeMemo, page)
          }
      }
    } ~ pathPrefix("about") {
      (get & path(Segment)) {
        (typeName) =>
          complete {
            memoProcess(aboutMemo, typeName)
          }
      }
    }
  }

  //  def getHomePosts(page: Int): RESTfulResponse[PagingDo] = {
  //    val homePosts = if (isExpire(homeMemo(page).queryAt)) {
  //      println("home posts update...")
  //      homeMemo.update(page)
  //    } else
  //      homeMemo(page)
  //    success(homePosts)
  //  }

}
