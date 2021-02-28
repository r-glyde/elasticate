package models

import io.circe.Encoder

final case class Movie(title: String)

object Movie {
  implicit val encoder: Encoder[Movie] = Encoder.forProduct1("title")(_.title)
}