package shine.st.blog.protocol.do_obj

import org.bson.types.ObjectId
import org.joda.time.DateTime

/**
  * Created by shinest on 20/01/2017.
  */
object PostDo {

  case class PostMetaDo(_id: ObjectId, title: String, subtitle: String, path: String, createAt: String, updateAt: Option[String], categoryId: List[String])

  case class PostDetailDo(postMetaDo: PostMetaDo, content: String, seo: Seo, queryAt: DateTime) extends TimeLimitDo

  case class PostDetailBackendDo(postMetaDo: PostMetaDo, md: String)

}

