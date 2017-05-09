package shine.st.blog.protocol.do_obj

import org.joda.time.DateTime

/**
  * Created by shinest on 22/02/2017.
  */
case class AboutDo(title: String, content: String, queryAt: DateTime) extends TimeLimitDo
