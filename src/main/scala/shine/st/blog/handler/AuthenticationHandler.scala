package shine.st.blog.handler

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import shine.st.blog.api.tokenKey
import shine.st.blog.dao.ManagerCollectionDao
import shine.st.blog.protocol.document.Manager
import shine.st.blog.protocol.input.TokenHeader
import spray.json.JsonParser

/**
  * Created by shinest on 22/04/2017.
  */
trait AuthenticationHandler {


  def createToken(secretKey: String, id: String): String = {
    val jwtClaim = JwtClaim(s"""{"id": "$id"}""").issuedNow.expiresIn(60 * 60 * 6)
    Jwt.encode(jwtClaim, tokenKey, JwtAlgorithm.HS256)
  }


  def authenticator(key: String): Credentials => Option[Manager] =
    credentials =>
      credentials match {
        case p@Credentials.Provided(token) =>
          Jwt.validate(token, key, Seq(JwtAlgorithm.HS256))
          val res0 = Jwt.decodeRaw(token, key, Seq(JwtAlgorithm.HS256))

          val json = JsonParser(res0.toOption.get)
          val tokenHeader = json.convertTo[TokenHeader]
          ManagerCollectionDao.findById(tokenHeader.id)

        case _ => None
      }


  def authenticate(tokenKey: String) =
    authenticateOAuth2("", authenticator(tokenKey))
}
