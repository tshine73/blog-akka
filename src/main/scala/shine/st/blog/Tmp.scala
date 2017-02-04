package shine.st.blog

import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.{Document, MongoCollection}
import shine.st.blog.dao.{CategoriesCollectionDao, PostCollectionDao}
import shine.st.blog.services.PagingService
import shine.st.blog.utils.MongoUtils

/**
  * Created by shinest on 16/01/2017.
  */
object Tmp {
  def main(args: Array[String]): Unit = {
    val collection: MongoCollection[Document] = MongoUtils.getCollection("test", "restaurants")
    val insertJson =
      """{
        |      "address" : {
        |         "street" : "133 Avenue",
        |         "zipcode" : "10075",
        |         "building" : "1480",
        |         "coord" : [ -73.9557413, 40.7720266 ]
        |      },
        |      "borough" : "Manhattan",
        |      "cuisine" : "Italian",
        |      "grades" : [
        |         {
        |            "date" : ISODate("2014-10-01T00:00:00Z"),
        |            "grade" : "A",
        |            "score" : 11
        |         },
        |         {
        |            "date" : ISODate("2014-01-16T00:00:00Z"),
        |            "grade" : "B",
        |            "score" : 17
        |         }
        |      ],
        |      "name" : "Vella",
        |      "restaurant_id" : "41704620"
        |   }""".stripMargin

    val mainCondition = and(equal("address.zipcode", "10075"), equal("address.street", "133 Avenue"))
    //
    //        collection.insertOne(Document(insertJson)).printHeadResult()
    //
    //        collection.find(mainCondition).projection(excludeId()).first().printHeadResult()
    //        collection.find(mainCondition).projection(and(excludeId(), include("borough"))).first().printHeadResult()

    //val post =    PostCollectionDao.allPosts()
    //    val post = PostCollectionDao.queryPost("scala_case_classes").map { post =>
    //      val content = IOUtils.inputStreamToString(S3.getObjectContent(bucketName, post.file))
    //      println(content)
    //      post
    //    }

    //update brief
    //    val all = PostCollectionDao.queryAllPosts()
    //    for (post <- all) {
    //      val content = IOUtils.inputStreamToString(S3.getObjectContent(bucketName, post.file))
    //      val r = PostCollectionDao.collection.updateOne(equal("file", post.file), set("brief", content.split("\n").zipWithIndex.filter(_._2 <= 5).map(_._1).mkString))
    //      println(r.getHeadResult)
    //    }
    // update brief end

    //    val parserISO: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
    //    val now = new DateTime()
    //    println(now)
    //    println(parserISO.print(now))


    //    val r = Await.result(post, Duration(10, TimeUnit.SECONDS))
    //
    //    println(r.categoryId.head)
    //    val category = CategoriesCollectionDao.queryCategory(r.categoryId.head)
    //    println(category)
    //
    //    val category2 = CategoriesService.findAllKeywords("scala.tutorial")
    //    println(category2)

    //println(ISODateTimeFormat.dateTimeNoMillis)
    //    val jsonString = """{ "_id" : { "oid" : "587b273184dd7ae5b7c63ee2" }, "title" : "Scala - Intro", "content_file" : "intro.md.html", "create_at" : { "$date" : 1443448991000 }, "update_at" : { "$date" : 1450640587000 }, "category_id" : ["scala.tutorial"], "brief" : 1 }
    //                       |""".stripMargin
    //    val jsonAst = jsonString.parseJson
    //    val post = jsonAst.convertTo[Post]
    //    println(post)

    val result1 = PagingService.categoryPaging("scala", 1)
//    println("finish")
//    val allCategories = CategoriesCollectionDao.findByAncestors("scala").toList
//
//    val result = PostCollectionDao.findByCategoryId(allCategories.map(_._id)).toList
    println(result1)
  }
}
