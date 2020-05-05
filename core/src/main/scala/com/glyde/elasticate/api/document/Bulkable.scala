package com.glyde.elasticate.api.document

import com.glyde.elasticate.api.ElasticRequest

private[document] trait Bulkable[+T] extends ElasticRequest[T] {
  def asBulk: BulkItem
}
