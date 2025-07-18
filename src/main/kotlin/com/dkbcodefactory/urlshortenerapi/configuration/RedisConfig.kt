package com.dkbcodefactory.urlshortenerapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement

@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, UrlAcknowledgement> {
        val template = RedisTemplate<String, UrlAcknowledgement>()
        template.connectionFactory = connectionFactory

        val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val jsonSerializer = Jackson2JsonRedisSerializer(objectMapper, UrlAcknowledgement::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jsonSerializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jsonSerializer

        return template
    }
}
