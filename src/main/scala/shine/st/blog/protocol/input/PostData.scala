package shine.st.blog.protocol.input

/**
  * Created by shinest on 29/04/2017.
  */
case class PostData(title: String, path: String, subtitle: String, categoryId: List[String], md: Option[String], content: Option[String])

