package shine.st.blog.protocol.doObj

import org.joda.time.DateTime

/**
  * Created by shinest on 20/01/2017.
  */
object PostDo {

  case class PostMetaDo(title: String, path: String, subtitle: String, createAt: DateTime, updateAt: Option[DateTime], briefContent: Option[String])

  case class PostDetailDo(postMetaDo: PostMetaDo, content: String, seo: Seo, queryAt: DateTime) extends TimeLimitDo

}

