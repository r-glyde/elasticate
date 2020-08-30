package com.elasticate.api.document

import com.elasticate.api.ElasticMethod
import com.elasticate.api.ElasticResponse.BasicResponse
import com.elasticate.utils.MapOps
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.syntax._

final case class Delete(index: String, id: String, additionalParams: Map[String, String] = Map.empty)
    extends BulkableRequest[Nothing] {

  override type Response = BasicResponse

  override implicit val responseDecoder: Decoder[BasicResponse] = deriveDecoder

  override def method: ElasticMethod = ElasticMethod.Delete
  override def endpoint: String      = s"$index/_doc/$id"
  override def body: Option[Nothing] = None
  override def asBulk: BulkItem = {
    val fields = Map(
      "_index" -> index,
      "_id"    -> id
    ) ++ additionalParams

    BulkItem(Map("delete" -> fields.strictMapValues(_.asJson)).asJson, None)
  }

}
