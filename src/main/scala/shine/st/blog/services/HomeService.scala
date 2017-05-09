package shine.st.blog.services

import org.joda.time.DateTime
import shine.st.blog.api.{bucketName, config}
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.do_obj.AboutDo
import shine.st.common.IOUtils
import shine.st.common.aws.S3

/**
  * Created by shinest on 22/02/2017.
  */
object HomeService {

  def homePaging(page: Int) = {
    val allPosts = PostCollectionDao.all.toList.sortWith {
      _.createAt.getMillis > _.createAt.getMillis
    }

    PagingService.postsPaging(allPosts, page)
  }

  def about(typeName: String) = {
    val s3FileName = config.getString(s"about.$typeName.file_name")
    val title = config.getString(s"about.$typeName.title")
    val content = IOUtils.inputStreamToString(S3.getObjectContent(bucketName, s3FileName))
    AboutDo(title, content, new DateTime)
  }
}
