package com.dkbcodefactory.urlshortenerapi.model

import com.dkbcodefactory.urlshortenerapi.dtos.UrlStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "url", schema = "url_shortener")
class UrlEntity (

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID,

    @Column(name = "original_url", unique = true, nullable = false)
    val originalUrl: String,

    @Column(name = "shorter_url")
    val shorterUrl: String?,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: UrlStatus,

    @Column(name = "description", columnDefinition = "TEXT", nullable = true, length = 1000000000)
    val description: String?,
) {
    constructor() : this(
        id = UUID.randomUUID(),
        originalUrl = "",
        shorterUrl = null,
        status = UrlStatus.RECEIVED,
        description = null
    )
}