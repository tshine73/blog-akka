package shine.st.blog.dao

import org.mongodb.scala.model.Filters.{equal, or}
import org.mongodb.scala.{Document, Observable}
import shine.st.blog.protocol._
import shine.st.blog.protocol.document.Post
import shine.st.blog.utils.MongoUtils.ImplicitObservable
import spray.json._

/**
  * Created by shinest on 16/01/2017.
  */
trait PostCollectionDao extends CollectionDao {
  override val collectionName: String = "post"

  val postObservable = find()

  implicit class PostObservable(val observable: Observable[Document]) extends ImplicitObservable[Document] {
    type R = Post
    override val converter: (Document) => R = (doc) => {
      val jsonSource = doc.toJson
      val jsonAst = jsonSource.parseJson
      jsonAst.convertTo[Post]
    }
  }

  def findAll() = {
    postObservable.getResults
  }

  def findByPath(path: String) = {
    find(equal("path", path)).headResult
  }

  def findByCategoryId(categoryId: List[String]) = {
    val condition = or(categoryId.map(c => equal("category_id", c)): _*)
    find(condition).getResults
  }

}

object PostCollectionDao extends PostCollectionDao