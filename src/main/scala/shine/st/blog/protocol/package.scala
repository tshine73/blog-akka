package shine.st.blog

import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import shine.st.blog.protocol.do_obj.PostDo.{PostDetailBackendDo, PostDetailDo, PostMetaDo}
import shine.st.blog.protocol.do_obj._
import shine.st.blog.protocol.document.{Categories, Manager, Post}
import shine.st.blog.protocol.input.{LoginInfo, PostData, TokenHeader}
import spray.json.{DefaultJsonProtocol, JsObject, JsValue, _}

/**
  * Created by shinest on 17/01/2017.
  */
package object protocol extends DefaultJsonProtocol {

  private[this] type IJF[T] = RootJsonFormat[T] // simple alias for reduced verbosity

  implicit def rESTfulResponseFormat[T: IJF]: IJF[RESTfulResponse[T]] = new RESTfulResponseFormat[T]

  class RESTfulResponseFormat[T: IJF] extends IJF[RESTfulResponse[T]] {
    def write(rest: RESTfulResponse[T]) = {
      if (rest.jsonResponse == Success)
        JsObject(
          "json_response" -> rest.jsonResponse.json,
          "payload" -> rest.payload.toJson
        )
      else
        JsObject("json_response" -> rest.jsonResponse.json)


    }

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

    override def write(obj: DateTime) = JsObject(("$date", JsString(parserISO.print(obj))))

    override def read(json: JsValue): DateTime = {
      json match {
        case j: JsObject =>
          new DateTime(j.getFields("$date").head.convertTo[Long]) //parserISO.parseDateTime(s)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }
  }


  //  mongodb document
  implicit val PostFormat = jsonFormat(Post, "_id", "title", "subtitle", "path", "file", "create_at", "update_at", "category_id", "brief")
  implicit val CategoriesFormat = jsonFormat6(Categories)
  implicit val ManagerFormat = jsonFormat(Manager, "_id", "password", "alias", "enabled", "create_at")

  //  domain object
  implicit val SeoFormat = jsonFormat1(Seo)

  implicit val PostMetaDoFormat = jsonFormat(PostMetaDo, "_id", "title", "path", "subtitle", "create_at", "update_at", "category_id")
  implicit val PostDetailDoFormat = jsonFormat(PostDetailDo, "post_meta_data", "content", "seo", "query_at")
  implicit val PostDetailBackendDoFormat = jsonFormat(PostDetailBackendDo, "post_meta_data", "md")

  implicit val PagingDoFormat = jsonFormat(PagingDo, "page", "post_meta_list", "seo", "query_at")
  implicit val CategoriesDoFormat = jsonFormat(CategoriesDo, "display", "description", "post_count", "paging_data")

  implicit val AboutFormat = jsonFormat3(AboutDo)

  implicit val TokenFormat = jsonFormat1(TokenDo)


  // input entity
  implicit val LoginInfoFormat = jsonFormat2(LoginInfo)
  implicit val PostDataFormat = jsonFormat6(PostData)

  //  header
  implicit val TokenHeaderFormat = jsonFormat3(TokenHeader)
}
