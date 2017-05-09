package shine.st.blog.dao

import org.mongodb.scala.{Document, FindObservable, Observable}
import shine.st.blog.protocol._
import shine.st.blog.protocol.document.Manager
import shine.st.blog.utils.MongoUtils.ImplicitObservable
import spray.json._

/**
  * Created by shinest on 19/01/2017.
  */
trait ManagerCollectionDao extends CollectionDao {
  override val collectionName: String = "manager"

  type T = Manager

  implicit class ManagerObservable(val observable: Observable[Document]) extends ImplicitObservable[Document] {
    type R = T
    override val converter: (Document) => R = (doc) => {
      val jsonSource = doc.toJson
      val jsonAst = jsonSource.parseJson
      jsonAst.convertTo[R]
    }
  }


  override def convert(observable: FindObservable[Document]) = observable.getResults

  //  FIXME: duplicate toJson
  override def toJson(data: T): JsValue = data.toJson

}

object ManagerCollectionDao extends ManagerCollectionDao
