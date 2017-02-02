package shine.st.blog

import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import shine.st.blog.protocol.doObj.PostDo.{PostDetailDo, PostMetaDo}
import shine.st.blog.protocol.doObj._
import shine.st.blog.protocol.document.{Categories, Post}
import spray.json.{DefaultJsonProtocol, JsObject, JsValue, _}

/**
  * Created by shinest on 17/01/2017.
  */
package object protocol extends DefaultJsonProtocol {

  private[this] type IJF[T] = RootJsonFormat[T] // simple alias for reduced verbosity

  implicit def rESTfulResponseFormat[T: IJF]: IJF[RESTfulResponse[T]] = new RESTfulResponseFormat[T]

  class RESTfulResponseFormat[T: IJF] extends IJF[RESTfulResponse[T]] {
    def write(rest: RESTfulResponse[T]) =
      if (rest.jsonResponse == Success)
        JsObject(
          "json_response" -> rest.jsonResponse.json,
          "payload" -> rest.payload.toJson
        )
      else
        JsObject("json_response" -> rest.jsonResponse.json)

    def read(value: JsValue) = deserializationError("no deserialization")

    // allows reading the JSON as a Some (useful in container formats)
    def readSome(value: JsValue) = Some(value.convertTo[T])
  }

  implicit object ObjectIdJsonFormat extends RootJsonFormat[ObjectId] {
    override def write(x: ObjectId) = JsString(x.toString)

    override def read(value: JsValue) =
      value match {
        case j: JsObject =>
          new ObjectId(j.getFields("$oid").head.convertTo[String])
        case x => deserializationError("Expected hex as ObjectId, but got " + x)
      }

  }

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {

    private val parserISO: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    override def write(obj: DateTime) = JsString(parserISO.print(obj))

    override def read(json: JsValue): DateTime = {
      json match {
        case j: JsObject =>
          new DateTime(j.getFields("$date").head.convertTo[Long]) //parserISO.parseDateTime(s)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }
  }


  //  entity
  implicit val postFormat = jsonFormat(Post, "_id", "title", "path", "file", "create_at", "update_at", "category_id", "brief")
  implicit val categoriesFormat = jsonFormat6(Categories)

  //  do
  implicit val seoFormat = jsonFormat1(Seo)

  implicit val postMetaDoFormat = jsonFormat4(PostMetaDo)
  implicit val postDetailDoFormat = jsonFormat4(PostDetailDo)

  implicit val pagingDoFormat = jsonFormat4(PagingDo)
  implicit val categoriesDoFormat = jsonFormat4(CategoriesDo)


}
