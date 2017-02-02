package shine.st.blog.services

import org.joda.time.DateTime
import shine.st.blog.MainService
import shine.st.blog.api.bucketName
import shine.st.blog.dao.PostCollectionDao
import shine.st.blog.protocol.doObj.PostDo.{PostDetailDo, PostMetaDo}
import shine.st.blog.protocol.doObj.Seo
import shine.st.blog.protocol.document.Post
import shine.st.common.IOUtils
import shine.st.common.aws.S3

/**
  * Created by shinest on 23/01/2017.
  */
object PostService {
  implicit val executor = MainService.executor

  def transToPostDetailDo(post: Post) = {
    val content = IOUtils.inputStreamToString(S3.getObjectContent(bucketName, post.file))
    val keywords = CategoriesService.findAllKeywords(post.categoryId)
    PostDetailDo(PostMetaDo(post.title, post.createAt, post.updateAt, post.brief), content, Seo(keywords), new DateTime())
  }

  def transToPostMetaDo(post: Post) = {
    PostMetaDo(post.title, post.createAt, post.updateAt, post.brief)
  }

  def getPost(path: String) = {
    println("get posts......")
    Thread.sleep(3000)
    PostCollectionDao.queryPost(path).map(PostService.transToPostDetailDo)
  }
}
