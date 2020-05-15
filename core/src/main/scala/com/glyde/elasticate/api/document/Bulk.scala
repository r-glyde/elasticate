package com.glyde.elasticate.api.document

import com.glyde.elasticate.api.ElasticMethod.Post
import com.glyde.elasticate.api.ElasticResponse.BulkResponse
import com.glyde.elasticate.api.{ElasticMethod, ElasticRequest}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

// TODO: types in bulk request are ugly
final case class Bulk[+T](messages: List[BulkableRequest[T]], additionalParams: Map[String, String] = Map.empty)
    extends ElasticRequest[String] {

  override type Response = BulkResponse

  override implicit val responseDecoder: Decoder[BulkResponse] = deriveDecoder

  override def method: ElasticMethod = Post
  override def endpoint: String      = "_bulk"
  override def body: Option[String]  = Option(messages.map(_.asBulk.asString).mkString("", "\n", "\n"))

}
