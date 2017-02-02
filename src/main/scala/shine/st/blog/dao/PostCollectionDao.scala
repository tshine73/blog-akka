package shine.st.blog.dao

import java.util.concurrent.TimeUnit

import org.joda.time.DateTime
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, Observable}
import shine.st.blog.protocol._
import shine.st.blog.protocol.document.Post
import shine.st.blog.utils.MongoUtils.ImplicitObservable
import spray.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

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

  def queryAllPosts() = {
    postObservable.getResults
  }

  def queryPost(path: String) = {
//    val t = find(equal("path", path))
//    val a = t.head()
//    val r = Await.result(t.head(), Duration(10, TimeUnit.SECONDS))
//    val r =  Await.result(t.head().map(Option(_)), Duration(10, TimeUnit.SECONDS))
//    val r = t.head()

    find(equal("path", path)).headResult()
//    Thread.sleep(1000)
//    println(r)
//    Post(new ObjectId,"dd","dd","",new DateTime(),None,Nil,2)
  }

}

object PostCollectionDao extends PostCollectionDao