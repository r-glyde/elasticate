package com.elasticate.api.document

import io.circe.Json

private[document] case class BulkItem(command: Json, source: Option[Json]) {
  def asString: String = (command, source) match {
    case (cmd, Some(entity)) => s"${cmd.noSpaces}\n${entity.noSpaces}"
    case (cmd, _)            => cmd.noSpaces
  }
}
