package shine.st.blog.dao

import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.{Document, Observable}
import shine.st.blog.protocol.document.Categories
import shine.st.blog.utils.MongoUtils.ImplicitObservable
import spray.json._

/**
  * Created by shinest on 19/01/2017.
  */
trait CategoriesCollectionDao extends CollectionDao {
  override val collectionName: String = "categories"

//  val mainCondition = and(equal("address.zipcode", "10075"), equal("address.street", "133 Avenue"))

  implicit class CategoriesObservable(val observable: Observable[Document]) extends ImplicitObservable[Document] {
    type R = Categories
    override val converter: (Document) => R = (doc) => {
      val jsonSource = doc.toJson
      val jsonAst = jsonSource.parseJson
      jsonAst.convertTo[Categories]
    }
  }

  def queryCategory(id: String) = {
    find(equal("_id", id)).getHeadResult
  }

}

object CategoriesCollectionDao extends CategoriesCollectionDao
