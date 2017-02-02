package shine.st.blog.services

import org.joda.time.DateTime
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.doObj.{PagingDo, Seo}
import shine.st.blog.protocol.document.Post

/**
  * Created by shinest on 25/01/2017.
  */
object PagingService {

  def postsPaging(posts: List[Post], page: Int, record: Int = 10) = {
    val specPosts = posts.slice((page - 1) * record, page * record)
    val postMetaList = specPosts.map(PostService.transToPostMetaDo)
    val keywords = specPosts.flatMap(_.categoryId).toSet.flatMap((c: String) => CategoriesService.findAllKeywords(c)).toList

    PagingDo(page, postMetaList, Seo(keywords), DateTime.now)
  }

  def postsPaging2(posts: List[Post]): (Int, Int) => PagingDo = (page, record) => {
    val specPosts = posts.slice((page - 1) * record, page * record)
    val postMetaList = specPosts.map(PostService.transToPostMetaDo)
    val keywords = specPosts.flatMap(_.categoryId).toSet.flatMap((c: String) => CategoriesService.findAllKeywords(c)).toList

    PagingDo(page, postMetaList, Seo(keywords), DateTime.now)
  }

  def homePaging(page: Int) = {
    val allPosts = PostCollectionDao.queryAllPosts.toList
    postsPaging(allPosts, page)
  }

  def homePaging(page: Int) = {
    val allPosts = PostCollectionDao.queryAllPosts.toList
    postsPaging(allPosts, page)
  }
}
