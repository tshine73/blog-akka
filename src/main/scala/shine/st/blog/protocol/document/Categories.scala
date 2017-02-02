package shine.st.blog.protocol.document

/**
  * Created by shinest on 17/01/2017.
  */
case class Categories(_id: String, ancestors: List[String], parent: Option[String], alias: String, description: String, keywords: String)