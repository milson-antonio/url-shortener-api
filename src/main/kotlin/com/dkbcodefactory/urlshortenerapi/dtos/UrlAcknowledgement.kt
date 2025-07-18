package com.dkbcodefactory.urlshortenerapi.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.UUID
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class UrlAcknowledgement(val id: UUID,
                         val originalUrl: String,
                         val shorterUrl: String?,
                         val status: UrlStatus,
                         val description: String?)
