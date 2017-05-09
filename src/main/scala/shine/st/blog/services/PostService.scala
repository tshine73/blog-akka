package shine.st.blog.services

import org.joda.time.DateTime
import shine.st.blog.MainService
import shine.st.blog.api.bucketName
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.NoData
import shine.st.blog.protocol.do_obj.PostDo.{PostDetailBackendDo, PostDetailDo, PostMetaDo}
import shine.st.blog.protocol.do_obj.Seo
import shine.st.blog.protocol.document.Post
import shine.st.blog.protocol.input.PostData
import shine.st.common.aws.S3
import shine.st.common.{DateTimeUtils, IOUtils}

import scala.concurrent.Future

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
    PostDetailDo(transToPostMetaDo(post), content, Seo(keywords), new DateTime())
  }

  def transToPostMetaDo(post: Post) = {
    PostMetaDo(post._id, post.title, post.path, post.subtitle, post.createAt, post.updateAt.map(format), post.categoryId)
  }

  def getPostByPath(path: String) = {
    //    FIXME
    Future(PostCollectionDao.findByPath(path)).map(PostService.transToPostDetailDo)
  }

  def getPostByIdBackend(id: String) = {
    val post = PostCollectionDao.findByObjectId(id).getOrElse(throw NoData("the post doesn't exist"))
    val content = IOUtils.inputStreamToString(S3.getObjectContent(bucketName, s"${post.path}.md"))
    PostDetailBackendDo(transToPostMetaDo(post), content)
  }

  def getAllPost() = {
    PostCollectionDao.all.map(transToPostMetaDo)
  }

}
