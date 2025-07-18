package com.dkbcodefactory.urlshortenerapi.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Bad Request")
class BadRequestException(message: String = "Bad Request") : RuntimeException(message)