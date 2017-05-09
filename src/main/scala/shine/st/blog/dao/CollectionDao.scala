package shine.st.blog.dao

import com.typesafe.config.ConfigFactory
import org.bson.types.ObjectId
import org.mongodb.scala.bson._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.{Document, FindObservable, MongoCollection}
import shine.st.blog.utils.MongoUtils
import shine.st.blog.utils.MongoUtils._
import spray.json._

/**
  * Created by shinest on 16/01/2017.
  */
trait CollectionDao {
  val collectionName: String

  val config = ConfigFactory.load

  lazy val collection: MongoCollection[Document] = MongoUtils.getCollection(config.getString("db.name"), collectionName)

  type T


  def convert(observable: FindObservable[Document]): Seq[T]

  def toJson(data: T): JsValue


  protected[dao] def find(filter: conversions.Bson) = {
    collection.find(filter)
  }

  def all() = {
    convert(collection.find())
  }

  def findByObjectId(id: String) = {
    convert(find(equal("_id", new ObjectId(id)))).headOption
  }

  def findById(id: String) = {
    convert(find(equal("_id", id))).headOption
  }

  def toDocument(data: T): Document = Document(JsObject(toJson(data).asJsObject.fields.filterKeys(_ != "_id")).compactPrint)

  def updateOne[V](key: String, value: V, data: T) = {
    val doc = toDocument(data)
    val update = doc.toList.map {
      case (field, data) => set(field, data)
    }

    val updates = combine(update: _*)
    collection.updateOne(equal(key, value), updates).getHeadResult.get
  }
}
