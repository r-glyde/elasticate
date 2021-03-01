import cats.syntax.all._
import io.circe.syntax._
import io.circe.parser.decode
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.search.builder.SearchSourceBuilder

package object data {

  implicit class RestClientOps(private val client: RestHighLevelClient) extends AnyVal {
    def insertMovie(movie: Movie, index: String): IndexResponse = {
      val request = new IndexRequest(index)
        .source(movie.asJson.noSpaces)

      client.index(request, RequestOptions.DEFAULT)
    }

    def findMovie(index: String): List[Movie] = {
      val request                     = new SearchRequest(index)
      val source: SearchSourceBuilder = new SearchSourceBuilder()

      val response = client.search(request.source(source), RequestOptions.DEFAULT)

      response.getHits.getHits.toList
        .traverse(res => decode[Movie](res.getSourceAsString))
        .getOrElse(List.empty)

    }
  }
}
