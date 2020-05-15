package com.glyde.elasticate.api

import io.circe.Decoder

abstract class ElasticRequest[+T] private[elasticate] {

  type Response

  implicit val responseDecoder: Decoder[Response]

  def method: ElasticMethod
  def endpoint: String
  def body: Option[T]
  def additionalParams: Map[String, String]

}
