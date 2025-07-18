package com.dkbcodefactory.urlshortenerapi.configuration.kafka

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
@Profile("!local")
class KafkaProducerConfig {

    @Bean
    fun producerFactory(): ProducerFactory<String, UrlAcknowledgement> {
        val configProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, UrlAcknowledgement> {
        return KafkaTemplate(producerFactory())
    }
}
