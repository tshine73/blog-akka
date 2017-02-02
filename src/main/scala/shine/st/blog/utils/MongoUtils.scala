package shine.st.blog.utils

/**
  * Created by shinest on 15/01/2017.
  */


import java.util.concurrent.TimeUnit

import org.mongodb.scala._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object MongoUtils {
  val mongoClient: MongoClient = MongoClient()

  def getCollection(dbName: String, collectionName: String) = mongoClient.getDatabase(dbName).getCollection(collectionName)

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    type R = String
    override val converter: (Document) => String = (doc) => doc.toJson

  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    type R = String
    override val converter: (C) => String = (doc) => doc.toString
  }

  trait ImplicitObservable[C] {
    type R
    val observable: Observable[C]
    val converter: (C) => R

    //    def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))

    //    def converResults(): Seq[R] = results.map(converter)

    //        def headResult() = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))
    //    def headResult() = observable.head().map(Option(_)).val.onComplete {
    //      case Success(value) => value
    //      case Failure(_) =>
    //    }

    def results() = observable.toFuture.map(_.map(converter))

    def headResult() = observable.head.map(converter)

    //    def printResults(initial: String = ""): Unit = {
    //      if (initial.length > 0) print(initial)
    //      results().foreach(res => println(converter(res)))
    //    }

    //    def printHeadResult(initial: String = ""): Unit = println(s"${initial}${converter(headResult())}")

    //    def getHeadResult: R = converter(headResult())

    def getHeadResult: R = awaitResult(headResult())

    def getResults: Seq[R] = awaitResult(results())

    private def awaitResult[A](f: Future[A]) = Await.result(f, Duration(10, TimeUnit.SECONDS))
  }

}