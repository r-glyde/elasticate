package data

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class Movie(title: String)

object Movie {
  implicit val codec: Codec[Movie] = deriveCodec
}