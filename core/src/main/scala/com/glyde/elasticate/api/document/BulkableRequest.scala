package com.glyde.elasticate.api.document

import com.glyde.elasticate.api.ElasticRequest

abstract class BulkableRequest[+T] private[document] () extends ElasticRequest[T] {
  def asBulk: BulkItem
}
