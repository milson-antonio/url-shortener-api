package com.dkbcodefactory.urlshortenerapi.service

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.dtos.UrlCreate
import com.dkbcodefactory.urlshortenerapi.dtos.UrlStatus
import com.dkbcodefactory.urlshortenerapi.exceptions.UrlNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class UrlService(
    private val persistenceService: UrlPersistenceService
) {
    private val logger = LoggerFactory.getLogger(UrlService::class.java)

     fun createShortUrl(urlCreate: UrlCreate): UrlAcknowledgement {
        val existing = persistenceService.findUrlByOriginal(urlCreate.originalUrl)
        if (existing != null) {
            logger.info("Existing URL returned: {}", urlCreate.originalUrl)
            return existing
        }

        val newUrl = UrlAcknowledgement(
            id = UUID.randomUUID(),
            originalUrl = urlCreate.originalUrl,
            shorterUrl = "",
            status = UrlStatus.RECEIVED,
            description = urlCreate.description ?: "URL created successfully"
        )

        persistenceService.saveAndPublish(newUrl)

        return newUrl
    }

    fun findByShortUrl(shorterUrl: String): UrlAcknowledgement {
        logger.info("Finding URL by short URL: {}", shorterUrl)
        return persistenceService.findUrlByShortUrl(shorterUrl)
            ?: throw UrlNotFoundException("URL with short URL '$shorterUrl' not found")
    }

    fun findById(id: UUID): UrlAcknowledgement {
        logger.info("Finding URL by ID: {}", id)
        return persistenceService.findById(id)
            ?: throw UrlNotFoundException("URL with ID '$id' not found")
    }

    fun deleteById(id: UUID) {
        logger.info("Deleting URL by ID: {}", id)
        val url = persistenceService.findById(id)
            ?: throw UrlNotFoundException("URL with ID '$id' not found")
        persistenceService.deleteById(id)
    }

    fun findAllPaginated(page: Int = 0, size: Int = 10): List<UrlAcknowledgement> {
        logger.info("Finding all URLs with pagination: page={}, size={}", page, size)
        return persistenceService.findAllPaginated(page, size)
    }
}