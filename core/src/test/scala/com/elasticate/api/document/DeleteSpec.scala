package com.elasticate.api.document

import com.elasticate.api.ElasticMethod
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DeleteSpec extends AnyWordSpec with Matchers {

  "Delete" should {
    "format request for elasticsearch" in {
      val index = "test-index"
      val id    = "id-1"

      val delete = Delete(index, id)

      delete.method shouldBe ElasticMethod.Delete
      delete.endpoint shouldBe s"$index/_doc/$id"
      delete.body shouldBe empty
    }
  }

}
