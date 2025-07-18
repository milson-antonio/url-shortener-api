package com.dkbcodefactory.urlshortenerapi.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ExceptionDto(
    val code: Int,
    val status: String,
    val localDateTime: LocalDateTime = LocalDateTime.now(),
    val title: String,
    val message: String? = null,
    val fieldWithError: List<FieldWithError>? = null
) {
    data class FieldWithError(
        val field: String,
        val detailedErrorMessage: String? = null
    )
}
