package com.elasticate.api.document

import com.elasticate.api.ElasticMethod.Post
import io.circe.syntax.EncoderOps
import data.Movie
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CreateSpec extends AnyWordSpec with Matchers with OptionValues {

  "Create" should {
    "format request for elasticsearch" in {
      val index = "test-index"
      val id    = "id-1"
      val movie = Movie("Titanic")

      val create = Create(index, id, movie)

      create.method shouldBe Post
      create.endpoint shouldBe s"$index/_create/$id"
      create.body.value shouldBe movie.asJson
    }
  }

}
