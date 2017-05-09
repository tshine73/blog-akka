package shine.st.blog.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.{path, pathPrefix, _}
import akka.http.scaladsl.server.Route
import org.bson.types.ObjectId
import shine.st.blog.dao.{CategoriesCollectionDao, ManagerCollectionDao, PostCollectionDao}
import shine.st.blog.handler.AuthenticationHandler
import shine.st.blog.protocol.do_obj.TokenDo
import shine.st.blog.protocol.input.{LoginInfo, PostData}
import shine.st.blog.protocol.{AccessDenied, _}
import shine.st.blog.services.PostService
import shine.st.common.aws.S3
import shine.st.common.{DateTimeUtils, HashUtils}

/**
  * Created by shinest on 16/03/2017.
  */
object BackendAPI extends BaseAPI with AuthenticationHandler {

  override def route: Route = {
    pathPrefix("authenticate") {
      post {
        entity(as[LoginInfo]) {
          loginInfo =>
            complete {
              val id = loginInfo.id

              val manager = ManagerCollectionDao.findById(id)

              if (manager.isEmpty || manager.get.password != HashUtils.sha256(loginInfo.password))
                throw AccessDenied

              val token = createToken(tokenKey, id)

              success(TokenDo(token))
            }
        }
      }
    } ~ authenticate(tokenKey) { user =>
      pathPrefix("post") {
        get {
          path("all") {
            pathEnd {
              complete {
                success(PostService.getAllPost())
              }

            }
          } ~ path(Segment) { id =>
            complete {
              success(PostService.getPostByIdBackend(id))
            }
          }
        } ~ post {
          (path(Segment) & entity(as[PostData])) { (id, postData) =>
            complete {
              val post = PostCollectionDao.findByObjectId(id).get
              val newFileName = s"${postData.path}.md.html"
              val updatePost = post.copy(title = postData.title, subtitle = postData.subtitle, path = postData.path, file = newFileName, categoryId = postData.categoryId, updateAt = Option(DateTimeUtils.now))
              //    update has result, example: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}
              val result = PostCollectionDao.updateOne("_id", new ObjectId(id), updatePost)

              (postData.path != post.path, postData.md) match {
                case (true, Some(md)) =>
                  S3.putObject(bucketName, s"${postData.path}.md", md)
                  S3.putObject(bucketName, newFileName, postData.content.get)
                  S3.deleteObject(bucketName, s"${post.path}.md")
                  S3.deleteObject(bucketName, post.file)
                case (false, Some(md)) =>
                  S3.putObject(bucketName, s"${postData.path}.md", md)
                  S3.putObject(bucketName, newFileName, postData.content.get)
                case (true, None) =>
                  S3.copyObject(bucketName, s"${post.path}.md", s"${postData.path}.md")
                  S3.copyObject(bucketName, post.file, newFileName)
                  S3.deleteObject(bucketName, s"${post.path}.md")
                  S3.deleteObject(bucketName, post.file)
                case (false, None) =>
              }

              "ok"
            }
          }
        }
      } ~ (get & path("categories")) {
        complete {
          success(CategoriesCollectionDao.all)
        }
      }
    }
  }

}
