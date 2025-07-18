package com.dkbcodefactory.urlshortenerapi.service

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.kafka.UrlProducer
import com.dkbcodefactory.urlshortenerapi.mapper.Mapper
import com.dkbcodefactory.urlshortenerapi.repository.UrlRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Service
class UrlPersistenceService(
    private val urlRepository: UrlRepository,
    private val redisService: RedisService,
    private val urlProducer: UrlProducer
) {
    private val logger = LoggerFactory.getLogger(UrlPersistenceService::class.java)

    fun findUrlByOriginal(originalUrl: String): UrlAcknowledgement? {
        val cachedUrlOptional = redisService.findByOriginalUrl(originalUrl)
        if (cachedUrlOptional.isPresent) {
            logger.info("URL found in Redis cache: {}", originalUrl)
            return cachedUrlOptional.get()
        }

        val entity = urlRepository.findByOriginalUrl(originalUrl)
        if (entity.isPresent) {
            val ack = Mapper.mapToUrlAcknowledgement(entity.get())
            redisService.saveUrl(ack)
            logger.info("URL found in database and cached in Redis: {}", originalUrl)
            return ack
        }
        return null
    }

    fun findUrlByShortUrl(shorterUrl: String): UrlAcknowledgement? {
        val cachedUrlOptional = redisService.findByShortUrl(shorterUrl)
        if (cachedUrlOptional.isPresent) {
            logger.info("URL found in Redis cache by short URL: {}", shorterUrl)
            return cachedUrlOptional.get()
        }

        val entity = urlRepository.findByShorterUrl(shorterUrl)
        if (entity.isPresent) {
            val ack = Mapper.mapToUrlAcknowledgement(entity.get())
            redisService.saveUrl(ack)
            logger.info("URL found in database by short URL and cached in Redis: {}", shorterUrl)
            return ack
        }
        return null
    }

    fun findById(id: UUID): UrlAcknowledgement? {
        val cachedUrlOptional = redisService.findById(id)
        if (cachedUrlOptional.isPresent) {
            logger.info("URL found in Redis cache by ID: {}", id)
            return cachedUrlOptional.get()
        }

        val entity = urlRepository.findById(id)
        if (entity.isPresent) {
            val ack = Mapper.mapToUrlAcknowledgement(entity.get())
            redisService.saveUrl(ack)
            logger.info("URL found in database by ID and cached in Redis: {}", id)
            return ack
        }
        return null
    }

    @Transactional
    fun saveAndPublish(urlAcknowledgement: UrlAcknowledgement) {
        CompletableFuture.runAsync {
            try {
                redisService.saveUrl(urlAcknowledgement)
                urlRepository.save(Mapper.mapToUrlEntity(urlAcknowledgement))
                urlProducer.sendUrl(urlAcknowledgement)
                logger.info("New URL persisted and sent: {}", urlAcknowledgement.originalUrl)
            } catch (ex: Exception) {
                logger.error("Error in saveAndPublish for URL {}: {}", 
                    urlAcknowledgement.originalUrl, ex.message, ex)
                // The URL is already saved to Redis and DB, so we don't need to roll back
                // The Kafka producer will retry sending the message
            }
        }
    }

    @Transactional
    fun deleteById(id: UUID) {
        val urlAcknowledgement = findById(id)
        if (urlAcknowledgement != null) {
            redisService.deleteUrl(urlAcknowledgement)
            urlRepository.deleteById(id)
            logger.info("URL deleted: {}", id)
        } else {
            logger.info("URL not found for deletion: {}", id)
        }
    }

    fun findAllPaginated(page: Int = 0, size: Int = 10): List<UrlAcknowledgement> {
        val pageable = org.springframework.data.domain.PageRequest.of(page, size)
        val entities = urlRepository.findAllWithShorterUrlAndSuccessStatus(pageable)

        return entities.content.map { entity ->
            Mapper.mapToUrlAcknowledgement(entity)
        }
    }
}
