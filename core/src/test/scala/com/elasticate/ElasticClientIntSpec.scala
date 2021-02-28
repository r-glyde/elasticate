package com.elasticate

import base.ElasticsearchWiremockBase
import com.elasticate.api.ElasticResponse.BasicResponse
import com.elasticate.api.document.Create
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import models.Movie
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Outcome}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

class ElasticClientIntSpec
    extends FixtureAnyWordSpec
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with ElasticsearchWiremockBase
    with Matchers {

  override type FixtureParam = ElasticClient[Identity]

  override protected def withFixture(test: OneArgTest): Outcome = {
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    test(new ElasticClient(s"http://$stubHost:$stubPort"))
  }

  "document:create" should {
    val index = "test-index"
    val id    = "id1"
    val movie = Movie("Titanic")

    "send post request to create new document" in { client =>
      val response = BasicResponse(index, "_doc", id, 1, "")

      stubPost(s"/$index/_create/$id", movie.asJson.noSpaces, 200, response.asJson.noSpaces)

      client.send(Create(index, id, movie)) shouldBe Right(response)
    }
  }
}
