package shine.st.blog.services

import org.joda.time.DateTime
import shine.st.blog.MainService
import shine.st.blog.api.bucketName
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.doObj.PostDo.{PostDetailDo, PostMetaDo}
import shine.st.blog.protocol.doObj.Seo
import shine.st.blog.protocol.document.Post
import shine.st.common.aws.S3
import shine.st.common.{DateTimeUtils, IOUtils}

/**
  * Created by shinest on 23/01/2017.
  */
object PostService {
  implicit val executor = MainService.executor

  implicit def format(dateTime: DateTime) = {
    DateTimeUtils.format(dateTime)(DateTimeUtils.DATE_HOUR_PATTERN)
  }


  def transToPostDetailDo(post: Post) = {
    val content = IOUtils.inputStreamToString(S3.getObjectContent(bucketName, post.file))
    val keywords = CategoriesService.findAllKeywords(post.categoryId)
    PostDetailDo(PostMetaDo(post.title, post.path, post.subtitle, post.createAt, post.updateAt.map(format), None), content, Seo(keywords), new DateTime())
  }

  def transToPostMetaDo(post: Post) = {
    PostMetaDo(post.title, post.path, post.subtitle, post.createAt, post.updateAt.map(format), Some(post.brief))
  }

  def getPost(path: String) = {
    PostCollectionDao.findByPath(path).map(PostService.transToPostDetailDo)
  }

}
