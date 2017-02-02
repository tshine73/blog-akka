package shine.st.blog.services

import shine.st.blog.dao.CategoriesCollectionDao
import shine.st.blog.protocol.document.Categories

/**
  * Created by shinest on 24/01/2017.
  */
object CategoriesService {
  def findAllKeywords(categoryName: String): List[String] = {
    val c = CategoriesCollectionDao.queryCategory(categoryName)
    c.keywords :: c.ancestors.flatMap(findAllKeywords)
  }

  def findAllKeywords(categoryNameList: List[String]): List[String] = {
    categoryNameList.flatMap(findAllKeywords)
  }

  def findGrandpa(categoryName: String): Categories = {
    val c = CategoriesCollectionDao.queryCategory(categoryName)
    if (c.ancestors.isEmpty)
      c
    else
      findGrandpa(c.ancestors.head)
  }
}
