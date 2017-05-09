package shine.st.blog.protocol.do_obj

import org.joda.time.DateTime

/**
  * Created by shinest on 03/02/2017.
  */
trait TimeLimitDo {
  def queryAt: DateTime
}
