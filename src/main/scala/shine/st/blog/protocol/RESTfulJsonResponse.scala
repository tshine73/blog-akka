package shine.st.blog.protocol

import spray.json._

/**
  * Created by steven.fanchiang  on 2016/10/27.
  */

case class RESTfulResponse[+T](jsonResponse: JsonResponse, payload: Option[T])

case class ResultBson(serverUsed: String, ok: Int, n: Int)


sealed trait JsonResponse extends Exception {
  def code: String

  def message: String

  def json = JsObject("returnCode" -> JsString(code), "message" -> JsString(message))
}


case object Success extends JsonResponse {
  override val code: String = "000"
  override val message: String = "success"
}

case class NoData(message: String) extends JsonResponse {
  override val code: String = "001"
}

case class InvalidParam(message: String) extends JsonResponse {
  override val code: String = "101"
}

case class UnknownError(message: String) extends JsonResponse {
  override val code: String = "999"
}

case class InCompleteParam(message: String) extends JsonResponse {
  override val code: String = "100"
}

case class DuplicateData(message: String) extends JsonResponse {
  override val code: String = "102"
}

case object InvalidAccessToken extends JsonResponse {
  override val code: String = "130"
  override val message: String = "invalid access token"
}

case object AccessDenied extends JsonResponse {
  override val code: String = "102"
  override val message: String = "accessToken incorrect"
}

