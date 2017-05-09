package shine.st.blog.dao

import org.mongodb.scala.model.Filters.{equal, or}
import org.mongodb.scala.{Document, FindObservable, Observable}
import shine.st.blog.protocol._
import shine.st.blog.protocol.document.Post
import shine.st.blog.utils.MongoUtils.ImplicitObservable
import spray.json._

/**
  * Created by shinest on 16/01/2017.
  */
trait PostCollectionDao extends CollectionDao {
  override val collectionName: String = "post"

  type T = Post

  implicit class PostObservable(val observable: Observable[Document]) extends ImplicitObservable[Document] {
    type R = T
    override val converter: (Document) => R = (doc) => {
      val jsonSource = doc.toJson
      val jsonAst = jsonSource.parseJson
      jsonAst.convertTo[R]
    }
  }

  def findByPath(path: String) = {
    //FIXME
    find(equal("path", path)).getHeadResult.get
  }

  def findByCategoryId(categoryId: List[String]) = {
    val condition = or(categoryId.map(c => equal("category_id", c)): _*)
    find(condition).getResults
  }

  override def convert(observable: FindObservable[Document]) = observable.getResults

  //  FIXME: duplicate toJson
  override def toJson(data: T): JsValue = data.toJson
}

object PostCollectionDao extends PostCollectionDao