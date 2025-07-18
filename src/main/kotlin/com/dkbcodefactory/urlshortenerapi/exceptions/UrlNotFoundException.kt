package com.dkbcodefactory.urlshortenerapi.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND, reason = "URL not found")
class UrlNotFoundException(message: String = "URL not found") : RuntimeException(message)
