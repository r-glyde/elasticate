package com.glyde.elasticate.api

import io.circe.Decoder

private[elasticate] trait ElasticRequest[+T] {

  type Response

  implicit val responseDecoder: Decoder[Response]

  def method: ElasticMethod
  def endpoint: String
  def body: Option[T]
  def additionalParams: Map[String, String]

}
