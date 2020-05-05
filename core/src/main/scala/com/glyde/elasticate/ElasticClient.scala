package com.glyde.elasticate

import java.nio.charset.StandardCharsets

import cats.Functor
import cats.syntax.either._
import com.glyde.elasticate.api.{ElasticMethod, ElasticRequest, Error, ErrorResponse}
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser._
import sttp.client._
import sttp.model.MediaType

class ElasticClient[F[_] : Functor](host: String)(implicit backend: SttpBackend[F, Nothing, NothingT]) {
  def send[T](request: ElasticRequest[T]): F[Response[Either[ErrorResponse, request.Response]]] = {

    import request.responseDecoder

    val endpoint = s"$host/${request.endpoint}"
    val uri      = uri"$endpoint?${request.additionalParams}"

    val baseRequest = request.method match {
      case ElasticMethod.Get    => basicRequest.get(uri)
      case ElasticMethod.Post   => basicRequest.post(uri)
      case ElasticMethod.Put    => basicRequest.put(uri)
      case ElasticMethod.Delete => basicRequest.delete(uri)
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
      .response(asStringAlways.map(str => {
        val error = ErrorResponse(Error(s"Response body cannot be interpreted: $str", "", ""))
        parse(str) match {
          case Left(_) => error.asLeft
          case Right(json) =>
            json.as[request.Response].fold(_ => json.as[ErrorResponse].getOrElse(error).asLeft, _.asRight)
        }
      }))
      .send()

  }
}
