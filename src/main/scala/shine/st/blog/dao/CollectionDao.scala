package shine.st.blog.dao

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.bson._
import org.mongodb.scala.{Document, MongoCollection}
import shine.st.blog.utils.MongoUtils

/**
  * Created by shinest on 16/01/2017.
  */
trait CollectionDao {
  val collectionName: String

  val config = ConfigFactory.load

  lazy val collection: MongoCollection[Document] = MongoUtils.getCollection(config.getString("db.name"), collectionName)


  protected[dao] def find(filter: conversions.Bson) = {
    collection.find(filter)
  }

  def find() = {
    collection.find()
  }
}
