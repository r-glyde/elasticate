package com.elasticate.api.index

import com.elasticate.api.ElasticMethod
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DeleteSpec extends AnyWordSpec with Matchers {

  "Delete" should {
    "format request for elasticsearch" in {
      val index = "test-index"

      val delete = Delete(index)

      delete.method shouldBe ElasticMethod.Delete
      delete.endpoint shouldBe index
      delete.body shouldBe empty
    }
  }

}
