package com.dkbcodefactory.urlshortenerapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@Configuration
@Profile("local")
class RedisContainerConfig {

    private val REDIS_PORT = 6379
    private val IMAGE_VERSION = "redis:7-alpine"
    private val DOCKER_IMAGE = DockerImageName.parse(IMAGE_VERSION)

    private var redisContainer: GenericContainer<*>? = null

    @Bean
    fun redisContainer(): GenericContainer<*> {
        if (redisContainer == null) {
            redisContainer = GenericContainer(DOCKER_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .apply { start() }
        }
        return redisContainer!!
    }

    @Bean
    fun redisConnectionFactory(redisContainer: GenericContainer<*>): RedisConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration().apply {
            hostName = redisContainer.host
            port = redisContainer.getMappedPort(REDIS_PORT)
        }
        return LettuceConnectionFactory(redisConfig)
    }
}