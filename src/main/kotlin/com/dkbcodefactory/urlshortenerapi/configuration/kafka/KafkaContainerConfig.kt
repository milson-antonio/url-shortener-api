package com.dkbcodefactory.urlshortenerapi.configuration.kafka

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

@Configuration
@Profile("local")
class KafkaContainerConfig {

    @Bean
    fun kafkaContainer(): KafkaContainer {
        val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"))
        container.start()
        return container
    }

    @Bean
    fun kafkaTemplate(kafkaContainer: KafkaContainer): KafkaTemplate<String, UrlAcknowledgement> {
        val configProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return KafkaTemplate(DefaultKafkaProducerFactory(configProps))
    }
}