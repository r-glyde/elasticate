package com.glyde.elasticate.api.document

import com.glyde.elasticate.api.ElasticMethod
import com.glyde.elasticate.api.ElasticMethod.{Post, Put}
import com.glyde.elasticate.api.ElasticResponse.BasicResponse
import io.circe._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.syntax._

final case class Index[T : Encoder](index: String,
                                    id: Option[String],
                                    entity: T,
                                    additionalParams: Map[String, String] = Map.empty)
    extends BulkableRequest[Json] {

  override type Response = BasicResponse

  override implicit val responseDecoder: Decoder[BasicResponse] = deriveDecoder

  override def method: ElasticMethod = if (id.isDefined) Put else Post
  override def endpoint: String      = s"$index/_doc/${id.getOrElse("")}"
  override def body: Option[Json]    = Option(entity.asJson)
  override def asBulk: BulkItem = {
    val fields = List(
      Option("_index" -> index.asJson),
      id.map(id => "_id" -> id.asJson)
    ).flatten ++ additionalParams.mapValues(_.asJson).toList

    BulkItem(Map("index" -> JsonObject(fields: _*)).asJson, Option(entity.asJson))
  }

}
