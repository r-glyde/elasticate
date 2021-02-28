package com.elasticate

import base.ContainerHealthcheck
import com.elasticate.api.ElasticResponse.BasicResponse
import com.elasticate.api.document.Create
import io.circe.syntax.EncoderOps
import models.Movie
import org.apache.http.HttpHost
import org.elasticsearch.client.{Request, RestClient}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, EitherValues, Outcome}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

class ElasticClientIntSpec
    extends FixtureAnyWordSpec
    with ContainerHealthcheck
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Matchers
    with EitherValues {

  override type FixtureParam = ElasticClient[Identity]

  val testClient: RestClient = RestClient.builder(new HttpHost("localhost", 9201)).build()

  override protected def withFixture(test: OneArgTest): Outcome = {
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    test(new ElasticClient(s"http://localhost:9201"))
  }

  override protected def afterAll(): Unit = testClient.close()

  override protected def afterEach(): Unit = testClient.performRequest(new Request("DELETE", "/_all"))

  "document:create" should {
    val index = "test-index"
    val id    = "id-1"
    val movie = Movie("Titanic")

    "create a new document" in { client =>
      val result = client.send(Create(index, id, movie))

      result shouldBe Right(BasicResponse(index, "_doc", id, 1, "created"))
    }

    "fail if document id already exists" in { client =>
      val firstCreate  = client.send(Create(index, id, movie))
      val secondCreate = client.send(Create(index, id, movie))

      firstCreate.isRight shouldBe true
      secondCreate.left.value.error.reason should include("document already exists")
    }
  }

  def storeMovie(index: String, movie: Movie): Unit = {
    val request = new Request("POST", s"$index/_doc")
    request.setJsonEntity(movie.asJson.noSpaces)
    testClient.performRequest(request)
  }
}
