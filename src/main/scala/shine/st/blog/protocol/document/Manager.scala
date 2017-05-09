package shine.st.blog.protocol.document

import org.joda.time.DateTime

/**
  * Created by shinest on 17/01/2017.
  */
case class Manager(_id: String, password: String, alias: String, enabled: Boolean, createAt: DateTime)