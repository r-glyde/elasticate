package com.elasticate.api.document

import com.elasticate.api.ElasticRequest

abstract class BulkableRequest[+T] private[document] () extends ElasticRequest[T] {
  def asBulk: BulkItem
}
