package com.elasticate

package object utils {

  private[elasticate] implicit class MapOps[K, V](val map: Map[K, V]) extends AnyVal {

    def strictMapValues[V2](f: V => V2): Map[K, V2] = map.map { case (k, v) => k -> f(v) }

  }

}
