package shine.st.blog.protocol.document

import org.bson.types.ObjectId
import org.joda.time.DateTime

/**
  * Created by shinest on 17/01/2017.
  */
case class Post(_id: ObjectId, title: String, subtitle:String, path: String, file: String, createAt: DateTime, updateAt: Option[DateTime], categoryId: List[String], brief: String)
