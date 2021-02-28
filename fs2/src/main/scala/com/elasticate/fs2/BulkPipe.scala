package com.elasticate.fs2

import cats.effect.{Concurrent, Timer}
import com.elasticate.ElasticClient
import com.elasticate.api.ElasticResponse.BulkResponse
import com.elasticate.api.document.{Bulk, BulkableRequest}
import fs2.Pipe
import io.circe.Json

import scala.concurrent.duration._

// Allow zipping response with requests
// On failure pass request back alongside error?
object BulkPipe {

  def apply[F[_] : Concurrent : Timer](client: ElasticClient[F],
                                       maxBatch: Int,
                                       maxWait: FiniteDuration): Pipe[F, BulkableRequest[Json], BulkResponse] =
    _.groupWithin(maxBatch, maxWait)
      .evalMap(reqs => client.send(Bulk(reqs.toList)))
      .collect { case Right(res) => res }

}
