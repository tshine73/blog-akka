package shine.st.blog.protocol.do_obj

import org.joda.time.DateTime
import shine.st.blog.protocol.do_obj.PostDo.PostMetaDo

/**
  * Created by shinest on 25/01/2017.
  */


case class PagingDo(page: Int, postMetaList: List[PostMetaDo], seo: Seo, queryAt: DateTime) extends TimeLimitDo