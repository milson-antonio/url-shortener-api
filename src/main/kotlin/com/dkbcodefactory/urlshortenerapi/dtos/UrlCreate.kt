package com.dkbcodefactory.urlshortenerapi.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UrlCreate(
    @field:NotBlank(message = "The URL must not be empty.")
    @field:Pattern(regexp = "^(https?://).+", message = "The URL must start with http:// or https://")
    val originalUrl: String
)