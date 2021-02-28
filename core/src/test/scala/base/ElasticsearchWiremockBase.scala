package base

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.Options
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

trait ElasticsearchWiremockBase { self: BeforeAndAfterAll with BeforeAndAfterEach =>

  val stubHost: String = "localhost"
  val stubPort: Int    = 9200

  private val wiremockConfig = wireMockConfig()
    .port(stubPort)
    .bindAddress(stubHost)
    .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER)

  private val wiremockServer = new WireMockServer(wiremockConfig)

  override def beforeAll(): Unit = {
    wiremockServer.start()
    WireMock.configureFor(stubHost, stubPort)
  }

  override def afterEach(): Unit = wiremockServer.resetMappings()

  override def afterAll(): Unit = wiremockServer.stop()

  def stubPost(endpoint: String, requestBody: String, status: Int, responseBody: String): StubMapping =
    stubFor(
      post(urlMatching(endpoint))
        .withRequestBody(equalToJson(requestBody))
        .willReturn(
          aResponse
            .withStatus(status)
            .withHeader("Content-Type", "application/json")
            .withBody(responseBody)
        )
    )

}
