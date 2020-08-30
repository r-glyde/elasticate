package com.elasticate.api.index

import com.elasticate.api.ElasticResponse.AcknowledgementResponse
import com.elasticate.api.{ElasticMethod, ElasticRequest}

final case class Delete(index: String, additionalParams: Map[String, String] = Map.empty)
    extends ElasticRequest[Nothing, AcknowledgementResponse] {

  override def method: ElasticMethod = ElasticMethod.Delete
  override def endpoint: String      = index
  override def body: Option[Nothing] = None

}
