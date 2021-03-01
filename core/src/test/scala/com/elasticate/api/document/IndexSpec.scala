package com.elasticate.api.document

import com.elasticate.api.ElasticMethod.{Post, Put}
import io.circe.syntax.EncoderOps
import data.Movie
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class IndexSpec extends AnyWordSpec with Matchers with OptionValues {

  "Index" should {
    val index = "test-index"
    val movie = Movie("Titanic")

    "format index request with id for elasticsearch" in {
      val id = "id-1"

      val request = Index(index, Some(id), movie)

      request.method shouldBe Put
      request.endpoint shouldBe s"$index/_doc/$id"
      request.body.value shouldBe movie.asJson
    }

    "format index request without id for elasticsearch" in {
      val request = Index(index, None, movie)

      request.method shouldBe Post
      request.endpoint shouldBe s"$index/_doc/"
      request.body.value shouldBe movie.asJson
    }
  }

}
