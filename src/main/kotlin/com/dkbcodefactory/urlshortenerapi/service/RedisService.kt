package com.dkbcodefactory.urlshortenerapi.service

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, UrlAcknowledgement>
) {
    companion object {
        private const val URL_KEY_PREFIX = "url:"
        private const val DEFAULT_EXPIRATION = 24L
    }

    fun saveUrl(urlAcknowledgement: UrlAcknowledgement) {
        val key = generateKey(urlAcknowledgement.originalUrl)
        redisTemplate.opsForValue().set(key, urlAcknowledgement, DEFAULT_EXPIRATION, TimeUnit.HOURS)
    }

    fun findByOriginalUrl(originalUrl: String): Optional<UrlAcknowledgement> {
        val key = generateKey(originalUrl)
        val urlAcknowledgement = redisTemplate.opsForValue().get(key)
        return Optional.ofNullable(urlAcknowledgement)
    }

    private fun generateKey(originalUrl: String): String {
        return "$URL_KEY_PREFIX$originalUrl"
    }
}