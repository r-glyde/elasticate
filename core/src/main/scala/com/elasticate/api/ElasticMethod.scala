package com.elasticate.api

sealed trait ElasticMethod extends Product with Serializable

object ElasticMethod {
  case object Get    extends ElasticMethod
  case object Post   extends ElasticMethod
  case object Put    extends ElasticMethod
  case object Delete extends ElasticMethod
}
