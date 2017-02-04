package shine.st.blog.services

import org.joda.time.DateTime
import shine.st.blog.dao.{CategoriesCollectionDao, PostCollectionDao}
import shine.st.blog.protocol.NoData
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

  def homePaging(page: Int) = {
    val allPosts = PostCollectionDao.findAll.toList
    postsPaging(allPosts, page)
  }

  def categoryPaging(categoryName: String, page: Int) = {
    val allCategories = CategoriesCollectionDao.findByAncestors(categoryName).toList
    if (allCategories.isEmpty)
      throw new NoData("category name not found")

    val specifiedCategory = PostCollectionDao.findByCategoryId(allCategories.map(_._id)).toList
    postsPaging(specifiedCategory, page)
  }
}
