package shine.st.blog.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import shine.st.blog.protocol.doObj.PostDo.PostDetailDo
import shine.st.blog.protocol.{NoData, RESTfulResponse}
import shine.st.blog.services.PostService
import shine.st.blog.utils.Memoize

import scala.concurrent.Future

/**
  * Created by shinest on 16/01/2017.
  */
object PostAPI extends BaseAPI {
  val postMemo = Memoize.memoize(PostService.getPost _)

  override def route: Route = {
    pathPrefix("post") {
      (get & path(Segment)) {
        postPath =>
          complete {
            getPost(postPath)
          }
      }
    }
  }

  //  def getPost(path: String): Future[RESTfulResponse[PostDetailDo]] = {
  //    PostCollectionDao.queryPost(path).map { post =>
  //      success(PostService.transToPostDetailDo(post))
  //    }.fallbackTo(Future(fail[PostDetailDo](NoData("no post data"))))
  //  }

  def getPost(path: String): Future[RESTfulResponse[PostDetailDo]] = {
    val post = postMemo(path).flatMap { p =>
      if (isExpire(p.queryAt)) {
        println("path info update...")
        postMemo.update(path)
      }
      else
        postMemo(path)
    }

    futureSuccess(post)(NoData("no post data"))
  }

}
