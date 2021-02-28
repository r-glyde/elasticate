package com.elasticate.examples

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import com.elasticate.ElasticClient
import com.elasticate.api.document.{BulkableRequest, Create, Delete, Index}
import com.elasticate.api.index
import com.elasticate.examples.model.{Book, Movie}
import com.elasticate.fs2.BulkPipe
import fs2.Stream
import io.circe.Json
import io.circe.generic.auto._
import org.http4s.client.asynchttpclient.AsyncHttpClient
import org.http4s.client.middleware.Logger
import sttp.client.http4s.Http4sBackend
import sttp.client.{NothingT, SttpBackend}

import scala.concurrent.duration._

object Fs2Demo extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      AsyncHttpClient.resource[IO]().use { client =>
        implicit val backend: SttpBackend[IO, fs2.Stream[IO, Byte], NothingT] =
          Http4sBackend.usingClient(Logger(logHeaders = true, logBody = true)(client), blocker)

        val esClient = new ElasticClient[IO]("http://localhost:9201")

        val movieIndex = "movies-http4s"
        val bookIndex  = "books-http4s"

        val bulkRequests: List[BulkableRequest[Json]] = List(
          Index(movieIndex, Option("abc"), Movie("Titanic", 1997)),
          Index(movieIndex, Option("def"), Movie("Fight Club", 1999)),
          Index(movieIndex, Option("ghi"), Movie("The Shawshank Redemption", 1994)),
          Delete(movieIndex, "abc"),
          Index(bookIndex, Option("abc"), Book("The Name of the Wind", "Patrick Rothfuss")),
          Create(bookIndex, "def", Book("The Final Empire", "Brandon Sanderson"))
        )

        val stream =
          Stream(bulkRequests: _*)
            .through(BulkPipe[IO](esClient, 2, 100.millis))
            .evalTap(res => IO { println(res) })
            .compile
            .drain

        (for {
          _ <- stream
          _ <- esClient.send(index.Delete(movieIndex))
          _ <- esClient.send(index.Delete(bookIndex))
        } yield ()).as(ExitCode.Success)
      }
    }
}
