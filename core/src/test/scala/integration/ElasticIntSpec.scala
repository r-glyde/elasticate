package integration

import base.ElasticIntegrationSpecBase
import com.glyde.elasticate.ElasticClient
import io.circe.{Encoder, Json}
import org.elasticsearch.client.Request
import org.scalatest.{BeforeAndAfterAllConfigMap, BeforeAndAfterEach, ConfigMap, EitherValues}
import sttp.client.HttpURLConnectionBackend
import utils.ContainerHealthcheck

class ElasticIntSpec
    extends ElasticIntegrationSpecBase
    with BeforeAndAfterAllConfigMap
    with BeforeAndAfterEach
    with EitherValues
    with ContainerHealthcheck {

  final case class Movie(title: String)

  object Movie {
    implicit val encoder: Encoder[Movie] = movie => Json.obj(("title", Json.fromString(movie.title)))
  }

  override protected def beforeAll(configMap: ConfigMap): Unit = {
    val elasticId = configMap.getRequired[String]("elasticsearch7:containerId")
    if (!containerReady(elasticId, "curl -I -s localhost:9200", _.contains("HTTP/1.1 200 OK")))
      throw new RuntimeException("Elasticsearch container not ready in time")
  }

  override protected def afterAll(configMap: ConfigMap): Unit = testClient.close()

  override protected def afterEach(): Unit = testClient.performRequest(new Request("DELETE", "/_all"))

  val movieIndex = "movies"

  "document api" should {
    import com.glyde.elasticate.api.document._

    "successfully bulk document requests together" in new TestContext {
      val bulkRequests = List(
        Index(movieIndex, Option("abc"), Movie("Titanic")),
        Create(movieIndex, "def", Movie("The Shawshank Redemption")),
        Delete(movieIndex, "abc")
      )

      val response = client.send(Bulk(bulkRequests))

      response.code.code shouldBe 200
      response.body.right.value.errors shouldBe false
    }
  }

  private class TestContext {

    implicit val sttpBackend = HttpURLConnectionBackend()
    val client               = new ElasticClient("http://localhost:9201")

    def storeMovie(movie: Movie): Unit = {
      val request = new Request("POST", s"$movieIndex/_doc")
      request.setJsonEntity(s"""{"title": "${movie.title}"}""")
      testClient.performRequest(request)
    }
  }
}
