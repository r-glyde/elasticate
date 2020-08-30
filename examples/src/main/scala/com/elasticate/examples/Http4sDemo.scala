package com.elasticate.examples

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import com.elasticate.ElasticClient
import com.elasticate.api.document._
import com.elasticate.api.index
import com.elasticate.examples.model.{Book, Movie}
import io.circe.generic.auto._
import io.circe.Json
import org.http4s.client.asynchttpclient.AsyncHttpClient
import org.http4s.client.middleware.Logger
import sttp.client.http4s.Http4sBackend

object Http4sDemo extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      AsyncHttpClient.resource[IO]().use { client =>
        implicit val backend = Http4sBackend.usingClient(Logger(logHeaders = true, logBody = true)(client), blocker)

        val esClient = new ElasticClient[IO]("http://localhost:9201")

        val movieIndex = "movies-http4s"
        val bookIndex  = "books-http4s"

        val requests = for {
          _ <- esClient.send(Index(movieIndex, Option("abc"), Movie("Titanic", 1997)))
          _ <- esClient.send(Index(movieIndex, Option("def"), Movie("Fight Club", 1999)))
          _ <- esClient.send(Create(movieIndex, "ghi", Movie("The Shawshank Redemption", 1994)))
          _ <- esClient.send(Create(movieIndex, "abc", Movie("The Dark Knight", 2008)))
          _ <- esClient.send(Delete(movieIndex, "abc"))
          _ <- esClient.send(index.Delete(movieIndex))
        } yield ()

        val bulkRequests: List[BulkableRequest[Json]] = List(
          Index(movieIndex, Option("abc"), Movie("Titanic", 1997)),
          Index(movieIndex, Option("def"), Movie("Fight Club", 1999)),
          Index(movieIndex, Option("ghi"), Movie("The Shawshank Redemption", 1994)),
          Delete(movieIndex, "abc"),
          Index(bookIndex, Option("abc"), Book("The Name of the Wind", "Patrick Rothfuss")),
          Create(bookIndex, "def", Book("The Final Empire", "Brandon Sanderson"))
        )

        (for {
          _   <- requests
          res <- esClient.send(Bulk(bulkRequests))
          _   <- IO { println(res.body.map(_.items)) }
          _   <- esClient.send(index.Delete(movieIndex))
          _   <- esClient.send(index.Delete(bookIndex))
        } yield ()).as(ExitCode.Success)
      }
    }

}
