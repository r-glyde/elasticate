package com.glyde.elasticate.api

import cats.syntax.functor._
import com.glyde.elasticate.api.ElasticResponse.BasicResponse
import io.circe.Decoder

sealed trait BulkItemResponse extends Product with Serializable

object BulkItemResponse {
  import io.circe.generic.auto._
  implicit val decodeBulkItemResponse: Decoder[BulkItemResponse] =
    List[Decoder[BulkItemResponse]](
      Decoder[IndexBulkResponse].widen,
      Decoder[CreateBulkResponse].widen,
      Decoder[DeleteBulkResponse].widen,
    ).reduceLeft(_ or _)

  final case class IndexBulkResponse(index: BasicResponse)   extends BulkItemResponse
  final case class CreateBulkResponse(create: BasicResponse) extends BulkItemResponse
  final case class DeleteBulkResponse(delete: BasicResponse) extends BulkItemResponse
}
