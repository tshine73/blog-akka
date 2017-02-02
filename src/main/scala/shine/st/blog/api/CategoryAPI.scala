package shine.st.blog.api

import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix, _}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.doObj.CategoriesDo
import shine.st.blog.services.CategoriesService
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

/**
  * Created by shinest on 16/01/2017.
  */


object CategoryAPI extends BaseAPI {
  override def route: Route = {
    pathPrefix("categories") {
      get {
        complete {
          count()
        }
      }
//      ~
//        (get & path(Segment)) {
//          categoryName =>
//            complete {
//              getPostsByCategoryName(categoryName)
//            }
//        }
    }
  }

  def count() = {
    success(
      PostCollectionDao.queryAllPosts
        .flatMap(_.categoryId)
        .groupBy(id => id)
        .mapValues(_.size)
        .toList.map { case (k, v) => CategoriesService.findGrandpa(k) -> v }
        .groupBy(_._1)
        .mapValues(_.foldLeft(0)((a, b) => a + b._2))
        .toList.map { case (categories, count) => CategoriesDo(categories.alias, categories.description, count, None) }
    )


  }

//  def getPostsByCategoryName(categoryName: String) = {
//    success(Nil[CategoriesDo])
//  }

}