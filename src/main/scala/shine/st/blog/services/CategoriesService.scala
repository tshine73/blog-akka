package shine.st.blog.services

import shine.st.blog.dao.CategoriesCollectionDao
import shine.st.blog.protocol.document.Categories

/**
  * Created by shinest on 24/01/2017.
  */
object CategoriesService {
  def findAllKeywords(categoryName: String): List[String] = {
    CategoriesCollectionDao.findById(categoryName) match {
      case Some(c) => c.keywords :: c.ancestors.flatMap(findAllKeywords)
      case None => Nil
    }

  }

  def findAllKeywords(categoryNameList: List[String]): List[String] = {
    categoryNameList.flatMap(findAllKeywords)
  }

  def findGrandpa(categoryName: String): Categories = {
    val c = CategoriesCollectionDao.findById(categoryName).get
    if (c.ancestors.isEmpty)
      c
    else
      findGrandpa(c.ancestors.head)
  }

  def findAllGranddaughter(categoryName: String): List[Categories] = {
    CategoriesCollectionDao.findByAncestors(categoryName).toList
  }
}
