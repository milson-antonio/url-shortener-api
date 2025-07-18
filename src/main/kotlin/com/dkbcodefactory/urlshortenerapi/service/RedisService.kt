package com.dkbcodefactory.urlshortenerapi.service

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, UrlAcknowledgement>
) {
    private val logger = LoggerFactory.getLogger(RedisService::class.java)

    companion object {
        private const val URL_KEY_PREFIX = "url:"
        private const val SHORT_URL_KEY_PREFIX = "short_url:"
        private const val ID_KEY_PREFIX = "id:"
        private const val DEFAULT_EXPIRATION = 24L
    }

    fun findById(id: UUID): Optional<UrlAcknowledgement> {
        val key = generateIdKey(id)
        val urlAcknowledgement = redisTemplate.opsForValue().get(key)
        return Optional.ofNullable(urlAcknowledgement)
    }

    fun findByOriginalUrl(originalUrl: String): Optional<UrlAcknowledgement> {
        val key = generateKey(originalUrl)
        val urlAcknowledgement = redisTemplate.opsForValue().get(key)
        return Optional.ofNullable(urlAcknowledgement)
    }

    fun findByShortUrl(shortUrl: String): Optional<UrlAcknowledgement> {
        val key = generateShortUrlKey(shortUrl)
        val urlAcknowledgement = redisTemplate.opsForValue().get(key)

        return if (urlAcknowledgement != null &&
                  !urlAcknowledgement.shorterUrl.isNullOrEmpty() && 
                  urlAcknowledgement.status == com.dkbcodefactory.urlshortenerapi.dtos.UrlStatus.SUCCESS) {
            Optional.of(urlAcknowledgement)
        } else {
            Optional.empty()
        }
    }

    private fun generateKey(originalUrl: String): String {
        return "$URL_KEY_PREFIX$originalUrl"
    }

    private fun generateShortUrlKey(shortUrl: String): String {
        return "$SHORT_URL_KEY_PREFIX$shortUrl"
    }

    private fun generateIdKey(id: UUID): String {
        return "$ID_KEY_PREFIX$id"
    }

    fun saveUrl(urlAcknowledgement: UrlAcknowledgement) {
        val key = generateKey(urlAcknowledgement.originalUrl)
        redisTemplate.opsForValue().set(key, urlAcknowledgement, DEFAULT_EXPIRATION, TimeUnit.HOURS)

        if (!urlAcknowledgement.shorterUrl.isNullOrEmpty()) {
            val shortKey = generateShortUrlKey(urlAcknowledgement.shorterUrl)
            redisTemplate.opsForValue().set(shortKey, urlAcknowledgement, DEFAULT_EXPIRATION, TimeUnit.HOURS)
        }

        val idKey = generateIdKey(urlAcknowledgement.id)
        redisTemplate.opsForValue().set(idKey, urlAcknowledgement, DEFAULT_EXPIRATION, TimeUnit.HOURS)
    }

    fun deleteUrl(urlAcknowledgement: UrlAcknowledgement) {
        val originalUrlKey = generateKey(urlAcknowledgement.originalUrl)
        redisTemplate.delete(originalUrlKey)

        if (!urlAcknowledgement.shorterUrl.isNullOrEmpty()) {
            val shortUrlKey = generateShortUrlKey(urlAcknowledgement.shorterUrl)
            redisTemplate.delete(shortUrlKey)
        }

        val idKey = generateIdKey(urlAcknowledgement.id)
        redisTemplate.delete(idKey)

        logger.info("Deleted URL from Redis cache: {}", urlAcknowledgement.id)
    }
}
