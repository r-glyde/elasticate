package com.elasticate

import java.nio.charset.StandardCharsets

import cats.Functor
import cats.syntax.either._
import cats.syntax.functor._
import com.elasticate.api.ElasticMethod._
import com.elasticate.api.{ElasticRequest, Error, ErrorResponse}
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser._
import sttp.client._
import sttp.model.MediaType

final class ElasticClient[F[_] : Functor](host: String)(implicit backend: SttpBackend[F, Nothing, NothingT]) {
  def send[T](request: ElasticRequest[T]): F[Either[ErrorResponse, request.Response]] = {

    import request.responseDecoder

    val endpoint     = s"$host/${request.endpoint}"
    val uri          = uri"$endpoint?${request.additionalParams}"
    val defaultError = (str: String) => ErrorResponse(Error(s"Response body cannot be interpreted: $str", "", ""))

    val baseRequest = request.method match {
      case Get    => basicRequest.get(uri)
      case Post   => basicRequest.post(uri)
      case Put    => basicRequest.put(uri)
      case Delete => basicRequest.delete(uri)
    }

    val req = request.body match {
      case Some(json: Json) =>
        val bytes = json.noSpaces.getBytes(StandardCharsets.UTF_8)
        baseRequest
          .body(bytes)
          .contentLength(bytes.length)
          .contentType(MediaType.ApplicationJson)
      case Some(v: String) =>
        val bytes = v.getBytes(StandardCharsets.UTF_8)
        baseRequest
          .body(bytes)
          .contentLength(bytes.length)
          .contentType("application/x-ndjson")
      case _ => baseRequest
    }

    req
      .response(asStringAlways)
      .send()
      .map { res =>
        if (res.isSuccess) decode[request.Response](res.body).leftMap(e => defaultError(e.getMessage))
        else decode[ErrorResponse](res.body).fold(e => defaultError(e.getMessage).asLeft, _.asLeft)
      }

  }
}
