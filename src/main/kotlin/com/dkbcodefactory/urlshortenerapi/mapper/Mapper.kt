package com.dkbcodefactory.urlshortenerapi.mapper

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.model.UrlEntity

class Mapper {
    companion object {
        fun mapToUrlEntity(urlAcknowledgement: UrlAcknowledgement): UrlEntity {
            return UrlEntity(
                id = urlAcknowledgement.id,
                originalUrl = urlAcknowledgement.originalUrl,
                shorterUrl = urlAcknowledgement.shorterUrl,
                status = urlAcknowledgement.status,
                description = urlAcknowledgement.description
            )
        }

        fun mapToUrlAcknowledgement(urlEntity: UrlEntity): UrlAcknowledgement {
            return UrlAcknowledgement(
                id = urlEntity.id,
                originalUrl = urlEntity.originalUrl,
                shorterUrl = urlEntity.shorterUrl,
                status = urlEntity.status,
                description = urlEntity.description
            )
        }
    }
}
