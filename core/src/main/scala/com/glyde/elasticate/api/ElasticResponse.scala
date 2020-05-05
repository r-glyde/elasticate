package com.glyde.elasticate.api

sealed trait ElasticResponse extends Product with Serializable

object ElasticResponse {

  final case class BasicResponse(_index: String, _type: String, _id: String, _version: Long, result: String)
      extends ElasticResponse
  final case class BulkResponse(took: Long, errors: Boolean, items: List[BulkItemResponse]) extends ElasticResponse
  final case class AcknowledgementResponse(acknowledged: Boolean)                           extends ElasticResponse

}
