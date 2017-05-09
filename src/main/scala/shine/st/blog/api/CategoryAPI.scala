package shine.st.blog.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix, _}
import akka.http.scaladsl.server.Route
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.do_obj.CategoriesDo
import shine.st.blog.services.{CategoriesService, PagingService}
import shine.st.blog.utils.Memoize

/**
  * Created by shinest on 16/01/2017.
  */


object CategoryAPI extends BaseAPI {
  val categoryMemo = Memoize.memoize(PagingService.categoryPaging _)

  override def route: Route = {
    parameter('update ? false) { update =>
      pathPrefix("categories") {
        get {
          pathEndOrSingleSlash {
            complete {
              count()
            }
          } ~
            path(Segment / IntNumber) {
              (categoryName, page) =>
                complete {
                  memoProcess(categoryMemo, (categoryName, page), update)
                }
            }
        }
      }
    }
  }

  def count() = {
    success(
      PostCollectionDao.all
        .flatMap(_.categoryId)
        .groupBy(id => id)
        .mapValues(_.size)
        .toList.map { case (k, v) => CategoriesService.findGrandpa(k) -> v }
        .groupBy(_._1)
        .mapValues(_.foldLeft(0)((a, b) => a + b._2))
        .toList.map { case (categories, count) => CategoriesDo(categories.alias, categories.description, count, None) }
    )


  }

  //  def getPostsByCategoryName(categoryName: String, page: Int) = {
  //    val categoryPosts =
  //      if (isExpire(categoryMemo((categoryName, page)).queryAt)) {
  //        println("specified category posts update...")
  //        categoryMemo.update((categoryName, page))
  //      } else
  //        categoryMemo((categoryName, page))
  //
  //    success(categoryPosts)
  //  }

}