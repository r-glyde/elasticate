package com.elasticate.examples

import com.elasticate.ElasticClient
import com.elasticate.api.document._
import com.elasticate.api.index
import com.elasticate.examples.model.{Book, Movie}
import io.circe.generic.auto._
import io.circe.Json
import sttp.client.HttpURLConnectionBackend

object SyncDemo extends App {

  implicit val sttpBackend = HttpURLConnectionBackend()

  val esClient = new ElasticClient("http://localhost:9201")

  val movieIndex = "movies-sync"
  val bookIndex  = "books-sync"

  val singleResponse = esClient.send(Index(movieIndex, Option("123"), Movie("Titanic", 1997)))
  println(s"Status: ${singleResponse.code}")
  println(s"Response Body: ${singleResponse.body}")

  val bulkRequests: List[BulkableRequest[Json]] = List(
    Create(movieIndex, "abc", Movie("Titanic", 1997)),
    Delete(movieIndex, "abc"),
    Index(bookIndex, Option("abc"), Book("The Name of the Wind", "Patrick Rothfuss")),
    Index(bookIndex, Option("def"), Book("The Final Empire", "Brandon Sanderson"))
  )
  val bulkResponse = esClient.send(Bulk(bulkRequests))
  println(s"Status: ${bulkResponse.code}")
  println(s"Response Body: ${bulkResponse.body}")

  esClient.send(index.Delete("*"))
  sttpBackend.close()
}