package com.glyde.elasticate.examples.http4s

import cats.effect.Bracket
import org.http4s.Headers
import org.http4s.client.Client
import org.http4s.headers.Authorization

object Auth {
  def apply[F[_]](auth: Authorization)(client: Client[F])(implicit F: Bracket[F, Throwable]): Client[F] =
    Client[F] { request =>
      client.run(request.withHeaders(request.headers ++ Headers.of(auth)))
    }
}
