package com.dkbcodefactory.urlshortenerapi.service

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.dtos.UrlCreate
import com.dkbcodefactory.urlshortenerapi.dtos.UrlStatus
import com.dkbcodefactory.urlshortenerapi.kafka.UrlProducer
import com.dkbcodefactory.urlshortenerapi.mapper.Mapper
import com.dkbcodefactory.urlshortenerapi.repository.UrlRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture
import org.slf4j.LoggerFactory

@Service
class UrlService(
    private val urlProducer: UrlProducer,
    private val urlRepository: UrlRepository,
    private val redisService: RedisService
) {
    private val logger = LoggerFactory.getLogger(UrlService::class.java)

    @Transactional
    public fun createShortUrl(urlCreate: UrlCreate): UrlAcknowledgement {
        val cachedUrlOptional = redisService.findByOriginalUrl(urlCreate.originalUrl)

        if (cachedUrlOptional.isPresent) {
            logger.info("URL found in Redis cache: {}", urlCreate.originalUrl)
            return cachedUrlOptional.get()
        }

        val existingUrlOptional = urlRepository.findByOriginalUrl(urlCreate.originalUrl)

        if (existingUrlOptional.isPresent) {
            val existingUrl = existingUrlOptional.get()
            val urlAcknowledgement = Mapper.mapToUrlAcknowledgement(existingUrl)

            redisService.saveUrl(urlAcknowledgement)
            logger.info("URL found in database and cached in Redis: {}", urlCreate.originalUrl)

            return urlAcknowledgement
        }

        val urlAcknowledgement = UrlAcknowledgement(
            id = UUID.randomUUID(),
            originalUrl = urlCreate.originalUrl,
            shorterUrl = "",
            status = UrlStatus.RECEIVED,
            message = "URL created successfully"
        )

        CompletableFuture.runAsync {
            redisService.saveUrl(urlAcknowledgement)
            logger.info("New URL created and cached in Redis: {}", urlCreate.originalUrl)
            val urlEntity = Mapper.mapToUrlEntity(urlAcknowledgement)
            urlRepository.save(urlEntity)
            urlProducer.sendUrl(urlAcknowledgement)
        }

        return urlAcknowledgement
    }

}
