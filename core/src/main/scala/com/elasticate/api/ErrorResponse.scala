package com.elasticate.api

final case class ErrorResponse(error: Error)
final case class Error(reason: String, `type`: String, index: String)
