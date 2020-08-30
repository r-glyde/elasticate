package com.elasticate.api.index

import com.elasticate.api.{ElasticMethod, ElasticRequest}
import com.elasticate.api.ElasticResponse.AcknowledgementResponse
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class Delete(index: String, additionalParams: Map[String, String] = Map.empty)
    extends ElasticRequest[Nothing] {

  override type Response = AcknowledgementResponse

  override implicit val responseDecoder: Decoder[AcknowledgementResponse] = deriveDecoder

  override def method: ElasticMethod = ElasticMethod.Delete
  override def endpoint: String      = index
  override def body: Option[Nothing] = None

}
