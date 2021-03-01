package com.elasticate

import base.ContainerHealthcheck
import com.elasticate.api.ElasticResponse.BasicResponse
import com.elasticate.api.document.Create
import data._
import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
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

  val testClient: RestHighLevelClient =
    new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9201)))

  override protected def withFixture(test: OneArgTest): Outcome = {
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    test(new ElasticClient(s"http://localhost:9201"))
  }

  override protected def afterAll(): Unit = testClient.close()

  override protected def afterEach(): Unit =
    testClient.indices().delete(new DeleteIndexRequest("_all"), RequestOptions.DEFAULT)

  "document:create" should {
    val index = "test-index"
    val id    = "id-1"
    val movie = Movie("Titanic")

    "create a new document" in { client =>
      client.send(Create(index, id, movie)) shouldBe Right(BasicResponse(index, "_doc", id, 1, "created"))
    }

    "fail if document id already exists" in { client =>
      val create       = Create(index, id, movie)
      val firstCreate  = client.send(create)
      val secondCreate = client.send(create)

      firstCreate.isRight shouldBe true
      secondCreate.left.value.error.reason should include("document already exists")
    }
  }
}
