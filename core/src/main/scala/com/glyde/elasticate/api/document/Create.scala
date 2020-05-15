package com.glyde.elasticate.api.document

import com.glyde.elasticate.api.ElasticMethod
import com.glyde.elasticate.api.ElasticMethod.Post
import com.glyde.elasticate.api.ElasticResponse.BasicResponse
import io.circe.generic.semiauto.deriveDecoder
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

final case class Create[T : Encoder](index: String,
                                     id: String,
                                     entity: T,
                                     additionalParams: Map[String, String] = Map.empty)
    extends BulkableRequest[Json] {

  override type Response = BasicResponse

  override implicit val responseDecoder: Decoder[BasicResponse] = deriveDecoder

  override def method: ElasticMethod = Post
  override def endpoint: String      = s"$index/_create/$id"
  override def body: Option[Json]    = Option(entity.asJson)
  override def asBulk: BulkItem = {
    val fields = Map(
      "_index" -> index,
      "_id"    -> id
    ) ++ additionalParams

    BulkItem(Map("create" -> fields.mapValues(_.asJson)).asJson, Option(entity.asJson))
  }

}
