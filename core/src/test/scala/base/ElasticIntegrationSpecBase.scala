package base

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.scalatest.Suite
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

trait ElasticIntegrationSpecBase extends AnyWordSpecLike with Matchers with Suite {

  val testClient = RestClient.builder(new HttpHost("localhost", 9201)).build()

}
