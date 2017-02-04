package shine.st.blog.dao

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, Observable}
import shine.st.blog.protocol.document.Categories
import shine.st.blog.utils.MongoUtils.ImplicitObservable
import spray.json._

/**
  * Created by shinest on 19/01/2017.
  */
trait CategoriesCollectionDao extends CollectionDao {
  override val collectionName: String = "categories"

  implicit class CategoriesObservable(val observable: Observable[Document]) extends ImplicitObservable[Document] {
    type R = Categories
    override val converter: (Document) => R = (doc) => {
      val jsonSource = doc.toJson
      val jsonAst = jsonSource.parseJson
      jsonAst.convertTo[Categories]
    }
  }

  def findById(id: String) = {
    find(equal("_id", id)).getHeadResult
  }

  def findByAncestors(someAncestor: String) = {
    find(equal("ancestors", someAncestor)).getResults
  }

}

object CategoriesCollectionDao extends CategoriesCollectionDao
